package com.example.order.controller;

import com.example.order.dto.request.OrderRequestDTO;
import com.example.order.dto.response.OrderResponseDTO;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/receive")
    public ResponseEntity<String> receiveOrder(@Valid @RequestBody OrderRequestDTO request) {
        orderService.receiveOrder(request);
        return ResponseEntity.ok("Order received successfully");
    }

    @GetMapping("/export")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
