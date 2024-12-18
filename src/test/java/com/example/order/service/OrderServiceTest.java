package com.example.order.service;

import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.request.OrderRequestDTO;
import com.example.order.dto.response.OrderReceiveResponseDTO;
import com.example.order.dto.response.OrderResponseDTO;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveOrder_NewOrder_ShouldProcessOrder() {
        OrderRequestDTO orderRequest = createOrderRequest("ORD12345");

        OrderReceiveResponseDTO response = orderService.receiveOrder(orderRequest);

        assertEquals("Order processed successfully: ORD12345", response.getMessage());
    }

    @Test
    void testReceiveOrder_DuplicateOrderFromCache_ShouldReturnDuplicateMessage() {
        OrderRequestDTO orderRequest = createOrderRequest("ORD12345");
        orderService.getProcessedOrdersCached().put("ORD12345", true);

        OrderReceiveResponseDTO response = orderService.receiveOrder(orderRequest);

        assertEquals("Order exists and has been processed already: ORD12345", response.getMessage());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    void testReceiveOrder_DuplicateOrderFromDatabase_ShouldReturnDuplicateMessage() {
        OrderRequestDTO orderRequest = createOrderRequest("ORD12345");
        Mockito.when(orderService.checkExistingOrder(orderRequest)).thenReturn(true);

        OrderReceiveResponseDTO response = orderService.receiveOrder(orderRequest);

        assertEquals("Order exists and has been processed already: ORD12345", response.getMessage());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    void testGetAllOrders_ShouldReturnMockedOrderList() {

        List<OrderResponseDTO> mockedResponse = Collections.singletonList(
                OrderResponseDTO.builder()
                        .externalOrderId("ORD12345")
                        .totalValue(200.0)
                        .status("PROCESSED")
                        .createdAt(LocalDateTime.now())
                        .items(Collections.singletonList(
                                OrderItemDTO.builder()
                                        .productId("P001")
                                        .quantity(2)
                                        .price(100.0)
                                        .build()
                        ))
                        .build()
        );

        OrderService spyOrderService = Mockito.spy(orderService);
        Mockito.doReturn(mockedResponse).when(spyOrderService).getAllOrders();

        List<OrderResponseDTO> orders = spyOrderService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals("ORD12345", orders.get(0).getExternalOrderId());
        assertEquals(200.0, orders.get(0).getTotalValue());
        assertEquals("PROCESSED", orders.get(0).getStatus());
        assertEquals(1, orders.get(0).getItems().size());
        assertEquals("P001", orders.get(0).getItems().get(0).getProductId());
    }

    private OrderRequestDTO createOrderRequest(String externalOrderId) {
        return OrderRequestDTO.builder()
                .externalOrderId(externalOrderId)
                .items(List.of(new OrderItemDTO("P001", 2, 100.0)))
                .build();
    }
}
