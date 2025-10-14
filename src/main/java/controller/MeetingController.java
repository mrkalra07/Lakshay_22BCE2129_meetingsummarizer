package com.example.meetingsummarizer.controller;

import com.example.meetingsummarizer.model.Meeting;
import com.example.meetingsummarizer.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*") // Allows requests from any origin
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/summarize")
    public ResponseEntity<String> uploadAndSummarize(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file.");
        }
        String summary = meetingService.processMeetingFile(file);
        return ResponseEntity.ok(summary);
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingService.findAllMeetings();
        return ResponseEntity.ok(meetings);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        Meeting meeting = meetingService.findMeetingById(id);
        return ResponseEntity.ok(meeting);
    }
}