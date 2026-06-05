package com.example.campconnect_backend.Dto;




import com.example.campconnect_backend.Entities.UserPreference.*;
import lombok.*;

public class UserPreferenceDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private Long userId;
        private String sports;
        private SkillLevel skillLevel;
        private int ageMin;
        private int ageMax;
        private Double latitude;
        private Double longitude;
        private String city;
        private double radiusKm;
        private String availability;
        private int groupSizeMin;
        private int groupSizeMax;
        private EngagementLevel engagementLevel;
        private String languages;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long userId;
        private String sports;
        private SkillLevel skillLevel;
        private int ageMin;
        private int ageMax;
        private Double latitude;
        private Double longitude;
        private String city;
        private double radiusKm;
        private String availability;
        private int groupSizeMin;
        private int groupSizeMax;
        private EngagementLevel engagementLevel;
        private String languages;
    }
}
