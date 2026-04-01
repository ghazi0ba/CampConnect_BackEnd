package com.example.campconnect_backend.Dto;

import lombok.*;

public class UserDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String username;
        private String email;
        private String password;
        private String avatarUrl;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private String avatarUrl;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRequest {
        private String username;
        private String avatarUrl;
    }
}