package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.Repositories.EquipmentRepository;
import com.example.campconnect_backend.Repositories.OrderRepository;
import com.example.campconnect_backend.Repositories.UserRepository;
import com.example.campconnect_backend.Services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    public OrderDto.Response create(OrderDto.CreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        List<Equipment> equipmentList = equipmentRepository.findAllById(request.getEquipmentIds());
        if (equipmentList.size() != request.getEquipmentIds().size()) {
            throw new EntityNotFoundException("One or more equipment IDs are invalid");
        }

        double totalAmount = equipmentList.stream()
                .mapToDouble(Equipment::getPrice)
                .sum();

        Order order = Order.builder()
                .user(user)
                .equipmentList(equipmentList)
                .totalAmount(totalAmount)
                .status(request.getStatus() != null ? request.getStatus() : OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto.Response getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto.Response> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public OrderDto.Response updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderDto.Response toResponse(Order order) {
        OrderDto.UserSummary userSummary = OrderDto.UserSummary.builder()
                .id(order.getUser().getId())
                .firstName(order.getUser().getFirstName())
                .lastName(order.getUser().getLastName())
                .email(order.getUser().getEmail())
                .build();

        List<OrderDto.EquipmentItem> equipmentItems = order.getEquipmentList().stream()
                .map(eq -> OrderDto.EquipmentItem.builder()
                        .id(eq.getId())
                        .name(eq.getName())
                        .price(eq.getPrice())
                        .build())
                .toList();

        return OrderDto.Response.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .user(userSummary)
                .equipment(equipmentItems)
                .build();
    }
}
