package com.example.order.dto.request;

import com.example.order.dto.OrderItemDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "External order ID must not be null")
    private String externalOrderId;

    @NotNull(message = "Items must not be null")
    @NotEmpty(message = "Items list must not be empty")
    private List<OrderItemDTO> items;
}

