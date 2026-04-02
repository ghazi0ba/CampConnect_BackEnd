package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.exception.BadRequestException;
import com.example.campconnect_backend.exception.ResourceNotFoundException;
import com.example.campconnect_backend.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;

    public List<OrderDto.Response> findByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public OrderDto.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public OrderDto.Response create(Long userId, OrderDto.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<Equipment> items = equipmentRepository.findAllById(request.getEquipmentIds());

        if (items.size() != request.getEquipmentIds().size()) {
            throw new BadRequestException("One or more equipment items not found");
        }

        List<Equipment> unavailable = items.stream()
                .filter(e -> !e.getAvailable())
                .collect(Collectors.toList());

        if (!unavailable.isEmpty()) {
            String names = unavailable.stream().map(Equipment::getName).collect(Collectors.joining(", "));
            throw new BadRequestException("Equipment not available: " + names);
        }

        double total = items.stream().mapToDouble(Equipment::getPrice).sum();

        Order order = Order.builder()
                .user(user)
                .equipmentList(items)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .build();

        // Mark equipment as unavailable
        items.forEach(e -> e.setAvailable(false));
        equipmentRepository.saveAll(items);

        return toResponse(orderRepository.save(order));
    }

    public OrderDto.Response updateStatus(Long id, String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        Order order = getOrThrow(id);
        order.setStatus(orderStatus);

        // If cancelled, make equipment available again
        if (OrderStatus.CANCELLED == orderStatus) {
            order.getEquipmentList().forEach(e -> e.setAvailable(true));
            equipmentRepository.saveAll(order.getEquipmentList());
        }

        return toResponse(orderRepository.save(order));
    }

    private Order getOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    private OrderDto.Response toResponse(Order o) {
        OrderDto.Response dto = new OrderDto.Response();
        dto.setId(o.getId());
        dto.setOrderDate(o.getOrderDate());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setStatus(o.getStatus());

        List<EquipmentDto.Response> eqDtos = o.getEquipmentList().stream().map(e -> {
            EquipmentDto.Response ed = new EquipmentDto.Response();
            ed.setId(e.getId());
            ed.setName(e.getName());
            ed.setDescription(e.getDescription());
            ed.setPrice(e.getPrice());
            ed.setAvailable(e.getAvailable());
            return ed;
        }).collect(Collectors.toList());

        dto.setEquipment(eqDtos);
        return dto;
    }
}
