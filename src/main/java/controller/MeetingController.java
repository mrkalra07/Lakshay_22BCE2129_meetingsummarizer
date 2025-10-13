package com.example.meetingsummarizer.controller;

import com.example.meetingsummarizer.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin
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
}