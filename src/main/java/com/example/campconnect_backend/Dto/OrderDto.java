package com.example.campconnect_backend.Dto;

import com.example.campconnect_backend.Entities.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long userId;

        @NotEmpty
        private List<ItemRequest> items;
    }

    @Data
    public static class ItemRequest {
        @NotNull
        private Long equipmentId;

        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentItem {
        private Long equipmentId;
        private String equipmentName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private OrderStatus status;
        private UserSummary user;
        private List<EquipmentItem> items;
    }
}
