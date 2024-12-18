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
public class OrderReceiveResponseDTO {
    private String message;
    private LocalDateTime createdAt;

    public static OrderReceiveResponseDTO toOrderReceiveResponseDTO(String message) {
        return OrderReceiveResponseDTO.builder()
                .message(message)
                .createdAt(LocalDateTime.now()).build();
    }
}

