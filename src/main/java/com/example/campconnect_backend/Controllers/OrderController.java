package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Entities.OrderStatus;
import com.example.campconnect_backend.Repositories.UserRepository;
import com.example.campconnect_backend.Services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<List<OrderDto.Response>> getMyOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.findByUser(getUserId(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDto.Response> create(@Valid @RequestBody OrderDto.Request request,
                                                    Authentication auth) {
        return ResponseEntity.ok(orderService.create(getUserId(auth), request));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(Arrays.asList(OrderStatus.values()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto.Response> updateStatus(@PathVariable Long id,
                                                          @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    private Long getUserId(Authentication auth) {
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow().getId();
    }
}
