package com.example.campconnect_backend.Dto;



import lombok.*;
import java.time.LocalDateTime;

public class MatchResultDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long groupMatchId;
        private String groupName;
        private String sport;
        private String location;
        private Double groupLatitude;
        private Double groupLongitude;
        private int maxParticipants;
        private long currentParticipants;
        private double compatibilityScore;   // 0-100
        private double sportScore;
        private double locationScore;
        private double skillScore;
        private double availabilityScore;
        private double groupSizeScore;
        private double distanceKm;
        private LocalDateTime scheduledAt;
        private String matchLabel;           // "Excellent", "Bon", "Moyen"
    }
}
