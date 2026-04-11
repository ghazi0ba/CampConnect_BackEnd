package com.example.campconnect_backend.Dto;

import com.example.campconnect_backend.Entities.EquipmentCategory;
import com.example.campconnect_backend.Entities.EquipmentSubCategory;
import jakarta.validation.constraints.*;
import lombok.*;

public class EquipmentDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        @Size(max = 120)
        private String name;

        @Size(max = 5000)
        private String description;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        private Double price;

        @NotNull
        private Boolean available;

        @NotNull
        private EquipmentSubCategory subCategory;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank
        @Size(max = 120)
        private String name;

        @Size(max = 5000)
        private String description;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        private Double price;

        @NotNull
        private Boolean available;

        @NotNull
        private EquipmentSubCategory subCategory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Boolean available;
        private EquipmentCategory category;
        private EquipmentSubCategory subCategory;
    }

    @Data
    public static class Filter {
        private String keyword;
        private EquipmentCategory category;
        private EquipmentSubCategory subCategory;
        private Boolean available;
        private Double minPrice;
        private Double maxPrice;
    }
}
