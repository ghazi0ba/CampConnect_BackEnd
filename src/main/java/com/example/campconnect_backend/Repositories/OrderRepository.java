package com.example.campconnect_backend.Repositories;

import com.example.campconnect_backend.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}

