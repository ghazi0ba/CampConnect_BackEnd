package com.example.campconnect_backend.Dto;

import lombok.*;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String message;
}



