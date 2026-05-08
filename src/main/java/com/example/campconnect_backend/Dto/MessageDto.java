package com.example.campconnect_backend.Dto;



import lombok.*;
import java.time.LocalDateTime;

public class MessageDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String content;
        private Long senderId;
        private Long receiverId;
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
        private Long receiverId;
        private String receiverUsername;
        private Long groupMatchId;
        private boolean isRead;
    }

    // Payload WebSocket
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WsPayload {
        private Long senderId;
        private Long receiverId;
        private Long groupMatchId;
        private String content;
    }
}