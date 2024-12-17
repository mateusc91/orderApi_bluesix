package com.example.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private String productId;
    private Integer quantity;
    private Double price;
}