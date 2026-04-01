package com.example.campconnect_backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MessageDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String content;
        private Long senderId;
        private Long groupMatchId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String content;
        private LocalDateTime sentAt;
        private Long senderId;
        private String senderUsername;
        private String senderAvatarUrl;
        private Long groupMatchId;
    }
}
