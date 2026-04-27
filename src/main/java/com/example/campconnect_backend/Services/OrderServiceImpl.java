package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.exception.BusinessValidationException;
import com.example.campconnect_backend.exception.ResourceNotFoundException;
import com.example.campconnect_backend.Repositories.EquipmentRepository;
import com.example.campconnect_backend.Repositories.OrderRepository;
import com.example.campconnect_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .deleted(false)
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDto.ItemRequest reqItem : request.getItems()) {
            Equipment equipment = equipmentRepository.findByIdAndDeletedFalse(reqItem.getEquipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + reqItem.getEquipmentId()));

            if (!Boolean.TRUE.equals(equipment.getAvailable())) {
                throw new BusinessValidationException("Equipment is not available: " + equipment.getName());
            }

            int requestedQty = reqItem.getQuantity();
            int availableQty = equipment.getTotalQuantity() - equipment.getReservedQuantity();

            if (requestedQty > availableQty) {
                throw new BusinessValidationException(
                        "Insufficient stock for equipment '" + equipment.getName() + "'. Available: " + availableQty
                );
            }

            // reserve stock
            equipment.setReservedQuantity(equipment.getReservedQuantity() + requestedQty);

            BigDecimal unitPrice = equipment.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(requestedQty));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .equipment(equipment)
                    .quantity(requestedQty)
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();

            items.add(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto.Response getById(Long id) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto.Response> getAll(Pageable pageable) {
        return orderRepository.findAllByDeletedFalse(pageable).map(this::toResponse);
    }

    @Override
    public OrderDto.Response updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        validateTransition(order.getStatus(), newStatus);

        // release reserved stock when cancelling
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Equipment equipment = item.getEquipment();
                int newReserved = equipment.getReservedQuantity() - item.getQuantity();
                equipment.setReservedQuantity(Math.max(0, newReserved));
            }
        }

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setDeleted(true);
        orderRepository.save(order);
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return;

        boolean allowed =
                (current == OrderStatus.PENDING && (next == OrderStatus.PAID || next == OrderStatus.CANCELLED)) ||
                        (current == OrderStatus.PAID && (next == OrderStatus.COMPLETED || next == OrderStatus.CANCELLED));

        if (!allowed) {
            throw new BusinessValidationException("Invalid status transition: " + current + " -> " + next);
        }
    }

    private OrderDto.Response toResponse(Order order) {
        OrderDto.UserSummary userSummary = OrderDto.UserSummary.builder()
                .id(order.getUser().getId())
                .firstName(order.getUser().getFirstName())
                .lastName(order.getUser().getLastName())
                .email(order.getUser().getEmail())
                .build();

        List<OrderDto.EquipmentItem> equipmentItems = order.getItems().stream()
                .map(item -> OrderDto.EquipmentItem.builder()
                        .equipmentId(item.getEquipment().getId())
                        .equipmentName(item.getEquipment().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        return OrderDto.Response.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .user(userSummary)
                .items(equipmentItems)
                .build();
    }
}
