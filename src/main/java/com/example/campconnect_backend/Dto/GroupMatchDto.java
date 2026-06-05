package com.example.campconnect_backend.Dto;

import lombok.*;
import java.time.LocalDateTime;

public class GroupMatchDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String name;
        private String description;
        private String sport;
        private int maxParticipants;
        private LocalDateTime scheduledAt;
        private String location;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String sport;
        private int maxParticipants;
        private LocalDateTime scheduledAt;
        private String location;
        private LocalDateTime createdAt;
        private long participantCount;
    }
}