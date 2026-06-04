package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Entities.OrderStatus;
import com.example.campconnect_backend.Services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto.Response create(@Valid @RequestBody OrderDto.CreateRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderDto.Response getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public Page<OrderDto.Response> getAll(
            @RequestParam(required = false) Long userId,
            Pageable pageable) {
        return orderService.getAll(userId, pageable);
    }

    @PatchMapping("/{id}/status")
    public OrderDto.Response updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
