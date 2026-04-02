package com.example.campconnect_backend.Dto;



import jakarta.validation.constraints.*;
import lombok.Data;

public class EquipmentDto {

    @Data
    public static class Request {
        @NotBlank private String name;
        private String description;
        @NotNull
        @Positive
        private Double price;
        @NotNull
        private Boolean available;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Boolean available;
    }
}

