package com.example.meetingsummarizer.dto;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {
    public static record Candidate(Content content) {}
    public static record Content(List<Part> parts, String role) {}
    public static record Part(String text) {}
}