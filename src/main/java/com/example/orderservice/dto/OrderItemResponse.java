package com.example.orderservice.dto;

import java.math.BigDecimal;

/** One product in the response, with its computed total. */
public record OrderItemResponse(
        String productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}