package com.example.campconnect_backend.Dto;

import com.example.campconnect_backend.Entities.EquipmentCategory;
import com.example.campconnect_backend.Entities.EquipmentSubCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EquipmentDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        @Size(max = 120)
        private String name;

        @Size(max = 5000)
        private String description;

        @NotNull
        @DecimalMin(value = "0.01")
        private BigDecimal price;

        @NotNull
        private Boolean available;

        @NotNull
        @Min(0)
        private Integer totalQuantity;

        @NotNull
        @Min(0)
        private Integer reservedQuantity;

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
        @DecimalMin(value = "0.01")
        private BigDecimal price;

        @NotNull
        private Boolean available;

        @NotNull
        @Min(0)
        private Integer totalQuantity;

        @NotNull
        @Min(0)
        private Integer reservedQuantity;

        @NotNull
        private EquipmentSubCategory subCategory;
    }

    @Data
    public static class PatchRequest {
        @Size(min = 1, max = 120)
        private String name;

        @Size(max = 5000)
        private String description;

        @DecimalMin(value = "0.01")
        private BigDecimal price;

        private Boolean available;

        @Min(0)
        private Integer totalQuantity;

        @Min(0)
        private Integer reservedQuantity;

        private EquipmentSubCategory subCategory;
    }

    @Data
    public static class StockAdjustmentRequest {
        @NotNull
        @Min(0)
        private Integer totalQuantity;

        @NotNull
        @Min(0)
        private Integer reservedQuantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long version;
        private String name;
        private String description;
        private BigDecimal price;
        private Boolean available;
        private Integer totalQuantity;
        private Integer reservedQuantity;
        private Integer availableQuantity;
        private Boolean inStock;
        private EquipmentCategory category;
        private EquipmentSubCategory subCategory;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Filter {
        private String keyword;
        private EquipmentCategory category;
        private EquipmentSubCategory subCategory;
        private Boolean available;  // manual availability
        private Boolean inStock;    // derived from quantities
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Boolean includeDeleted;
    }
}
