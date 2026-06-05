package com.example.campconnect_backend.Dto;

import com.example.campconnect_backend.Entities.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

public class UserDto {

    @Data
    public static class CreateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private Role role;
        // Matching / messaging profile (optional)
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class Response {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Role role;
        // Matching / messaging profile
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @Email
        @NotBlank
        private String email;
        private String phone;
        // Optional profile updates
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;
        @NotBlank
        @Size(min = 6)
        private String newPassword;
        @NotBlank
        private String confirmPassword;
    }
}
