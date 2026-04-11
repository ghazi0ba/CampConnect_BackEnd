package com.example.campconnect_backend.Dto;

import com.example.campconnect_backend.Entities.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long userId;

        @NotEmpty
        private List<Long> equipmentIds;

        // optional (default handled in service: PENDING)
        private OrderStatus status;
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
        private Long id;
        private String name;
        private Double price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDateTime orderDate;
        private Double totalAmount;
        private OrderStatus status;
        private UserSummary user;
        private List<EquipmentItem> equipment;
    }
}
