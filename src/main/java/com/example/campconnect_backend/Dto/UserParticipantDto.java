package com.example.campconnect_backend.Dto;
 

import com.example.campconnect_backend.Entities.ParticipantRole;
import com.example.campconnect_backend.Entities.ParticipantStatus;
import lombok.*;
import java.time.LocalDateTime;
 

 
public class UserParticipantDto {
 
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private Long userId;
        private Long groupMatchId;
        private ParticipantRole role;
        private ParticipantStatus status;
        private int score;
    }
 
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long userId;
        private String username;
        private String avatarUrl;
        private Long groupMatchId;
        private String groupMatchName;
        private ParticipantRole role;
        private ParticipantStatus status;
        private LocalDateTime joinedAt;
        private int score;
    }
 
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRequest {
        private ParticipantRole role;
        private ParticipantStatus status;
        private int score;
    }
}