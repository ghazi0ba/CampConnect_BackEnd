package com.example.campconnect_backend.Dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Size(min = 6)
        private String password;
        private String phone;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String email;
        private String role;
        private Long userId;

        public AuthResponse(String token, String email, String role, Long userId) {
            this.token = token;
            this.email = email;
            this.role = role;
            this.userId = userId;
        }
    }
}

