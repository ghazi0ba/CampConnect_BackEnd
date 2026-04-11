package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto.Response create(OrderDto.CreateRequest request);
    OrderDto.Response getById(Long id);
    Page<OrderDto.Response> getAll(Pageable pageable);
    OrderDto.Response updateStatus(Long id, String status);
    void delete(Long id);
}
