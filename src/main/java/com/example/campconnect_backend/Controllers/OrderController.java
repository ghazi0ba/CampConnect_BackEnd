package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto.Response> create(@Valid @RequestBody OrderDto.CreateRequest request) {
        return ResponseEntity.ok(orderService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ResponseEntity<Page<OrderDto.Response>> getAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    @PatchMapping("/{id}/status")
    //@PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ResponseEntity<OrderDto.Response> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN','AGENT')")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
