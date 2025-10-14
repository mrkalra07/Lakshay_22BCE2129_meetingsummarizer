package com.example.meetingsummarizer.service;

import com.example.meetingsummarizer.dto.GeminiRequest;
import com.example.meetingsummarizer.dto.GeminiResponse;
import com.example.meetingsummarizer.model.Meeting;
import com.example.meetingsummarizer.repository.MeetingRepository;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class MeetingService {

    private final WebClient webClient;
    private final MeetingRepository meetingRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public MeetingService(WebClient.Builder webClientBuilder, MeetingRepository meetingRepository) {
        this.webClient = webClientBuilder.build();
        this.meetingRepository = meetingRepository;
    }

    public String processMeetingFile(MultipartFile file) throws Exception {
        // Step 1: Transcribe the audio
        System.out.println("Calling Google Speech-to-Text API (long-running)...");
        String transcript = transcribeAudio(file);
        System.out.println("Transcription successful.");

        // Step 2: Summarize the transcript
        System.out.println("Calling Gemini API to summarize the transcript...");
        String summary = summarizeTranscript(transcript);
        System.out.println("Summarization successful.");

        // Step 3: Save the results to the database
        Meeting meeting = new Meeting();
        meeting.setOriginalFileName(file.getOriginalFilename());
        meeting.setTranscript(transcript);
        meeting.setSummary(summary);
        meetingRepository.save(meeting);
        System.out.println("Meeting summary saved to the database.");

        return summary;
    }

    // Method to fetch all saved meetings
    public List<Meeting> findAllMeetings() {
        return meetingRepository.findAllByOrderByCreatedAtDesc();
    }

    // Method to find a single meeting by its ID
    public Meeting findMeetingById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));
    }

    private String transcribeAudio(MultipartFile audioFile) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(audioFile.getBytes());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speechClient.longRunningRecognizeAsync(config, audio);

            LongRunningRecognizeResponse result = response.get();

            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult res : result.getResultsList()) {
                transcript.append(res.getAlternativesList().get(0).getTranscript());
            }
            return transcript.toString();
        }
    }

    private String summarizeTranscript(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return "Could not generate a summary because the transcript was empty.";
        }

        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + geminiApiKey;
        String prompt = "First, provide a brief one-paragraph summary of the following meeting transcript. " +
                        "Then, list the key decisions and action items. " +
                        "Format the entire output as Markdown. " +
                        "Use the headings '## Summary', '## Key Decisions', and '## Action Items'. " +
                        "Use bullet points for the decision and action item lists. " +
                        "Transcript:\n\n" + transcript;

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest requestBody = new GeminiRequest(List.of(content));

        try {
            GeminiResponse response = webClient.post()
                    .uri(apiUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            if (response != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "Error: Could not get summary from Gemini API.";
        }
        return "Error: No summary was returned from the API.";
    }
}