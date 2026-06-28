package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Created order details, including every item and the grand total. */
@Schema(description = "Created order details")
public record OrderResponse(
        String orderId,
        String customerId,
        List<OrderItemResponse> items,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String status
) {}