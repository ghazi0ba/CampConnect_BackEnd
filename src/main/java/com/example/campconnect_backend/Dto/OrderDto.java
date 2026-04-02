package com.example.campconnect_backend.Dto;



import com.example.campconnect_backend.Entities.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

import java.util.List;

public class OrderDto {

    @Data
    public static class Request {
        @NotEmpty
        private List<Long> equipmentIds;
    }

    @Data
    public static class Response {
        private Long id;
        private LocalDateTime orderDate;
        private Double totalAmount;
        private OrderStatus status;
        private List<EquipmentDto.Response> equipment;
    }
}

