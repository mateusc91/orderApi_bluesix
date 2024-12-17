package com.example.order.dto.response;

import com.example.order.dto.OrderItemDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private String externalOrderId;
    private Double totalValue;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}

