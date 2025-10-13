package com.example.meetingsummarizer.service;

import com.example.meetingsummarizer.dto.GeminiRequest;
import com.example.meetingsummarizer.dto.GeminiResponse;
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

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public MeetingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String processMeetingFile(MultipartFile file) throws Exception {
        // Step 1: Transcribe the audio file using Google Speech-to-Text
        System.out.println("Calling Google Speech-to-Text API (long-running)...");
        String transcript = transcribeAudio(file);
        System.out.println("Transcription successful.");
        System.out.println("Transcript: " + transcript);

        // Step 2: Summarize the transcript using Google Gemini
        System.out.println("Calling Gemini API to summarize the transcript...");
        String summary = summarizeTranscript(transcript);
        System.out.println("Summarization successful.");

        return summary;
    }

    private String transcribeAudio(MultipartFile audioFile) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(audioFile.getBytes());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3) // Change if your audio format is different
                    .setSampleRateHertz(16000) // Change if your audio sample rate is different
                    .setLanguageCode("en-US")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Use the asynchronous method for files larger than 1 minute
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speechClient.longRunningRecognizeAsync(config, audio);

            // Wait for the operation to complete
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
        String prompt = "Summarize the following transcript. " +
                        "Format the output as Markdown. " +
                        "Use a '## Key Decisions' heading and a '## Action Items' heading. " +
                        "Use bullet points for each list item. " +
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