package com.example.order.service;

import com.example.order.OrderStatus;
import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.request.OrderRequestDTO;
import com.example.order.dto.response.OrderResponseDTO;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private static final ConcurrentHashMap<String, Boolean> processedOrdersCached = new ConcurrentHashMap<>();

    @Cacheable(value = "orderCache", key = "#orderRequest.externalOrderId", unless = "#result == null || #result == false")
    public void receiveOrder(OrderRequestDTO orderRequest) {
        boolean existingOrder = checkExistingOrder(orderRequest);
        if (existingOrder) {
            log.warn("Order is a duplicate and already processed: {}", orderRequest.getExternalOrderId());
        }
        checkExistingOrder(orderRequest);
    }

    private boolean checkExistingOrder(OrderRequestDTO orderRequest) {
        if (processedOrdersCached.containsKey(orderRequest.getExternalOrderId()) ||
                orderRepository.existsByExternalOrderId(orderRequest.getExternalOrderId())) {
            return true;
        }

        processedOrdersCached.put(orderRequest.getExternalOrderId(), true);
        processOrder(orderRequest);

        return false;
    }

    private void processOrder (OrderRequestDTO orderRequest){
        Double totalValue = orderRequest.getItems()
                .stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        Order order = Order.builder()
                .externalOrderId(orderRequest.getExternalOrderId())
                .totalValue(totalValue)
                .status(OrderStatus.PROCESSED.name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(orderRequest.getItems().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        order.getItems().forEach(item -> item.setOrder(order));
        log.info("Order processed: " + orderRequest.getExternalOrderId());
        orderRepository.save(order);
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> OrderResponseDTO.builder()
                .externalOrderId(order.getExternalOrderId())
                .totalValue(order.getTotalValue())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                item.getProductId(), item.getQuantity(), item.getPrice()))
                        .collect(Collectors.toList()))
                .build()).collect(Collectors.toList());
    }
}
