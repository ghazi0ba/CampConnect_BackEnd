package com.example.campconnect_backend.Repositories;

import com.example.campconnect_backend.Entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndDeletedFalse(Long id);
    Page<Order> findAllByDeletedFalse(Pageable pageable);
    Page<Order> findAllByUserIdAndDeletedFalse(Long userId, Pageable pageable);
}
