package com.example.order.service;

import com.example.order.OrderStatus;
import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.request.OrderRequestDTO;
import com.example.order.dto.response.OrderReceiveResponseDTO;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final Map<String, Boolean> processedOrdersCached = new ConcurrentHashMap<>();
    private String responseMessage = "";

    public OrderReceiveResponseDTO receiveOrder(OrderRequestDTO orderRequest) {
        boolean existingOrder = checkExistingOrder(orderRequest);

        if (existingOrder) {
            responseMessage = "Order exists and has been processed already: " + orderRequest.getExternalOrderId();
            log.warn(responseMessage.toString());
        }
        return OrderReceiveResponseDTO.toOrderReceiveResponseDTO(responseMessage.toString());
    }

    @Cacheable(value = "orderCache", key = "#orderRequest.externalOrderId", unless = "#result == false")
    public boolean checkExistingOrder(OrderRequestDTO orderRequest) {
        log.info("Checking if order exists: {}", orderRequest.getExternalOrderId());

        if (processedOrdersCached.containsKey(orderRequest.getExternalOrderId())) {
            return true;
        }

        boolean orderExistsInDb = orderRepository.existsByExternalOrderId(orderRequest.getExternalOrderId());
        if (orderExistsInDb) {
            processedOrdersCached.put(orderRequest.getExternalOrderId(), true);
            return true;
        }

        processedOrdersCached.put(orderRequest.getExternalOrderId(), true);
        processOrder(orderRequest);
        return false;
    }

    private void processOrder(OrderRequestDTO orderRequest) {
        log.info("Processing order: {}", orderRequest.getExternalOrderId());

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
        responseMessage = "Order processed successfully: " + orderRequest.getExternalOrderId();
        orderRepository.save(order);
        log.info("Order processed and saved: {}", orderRequest.getExternalOrderId());
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
