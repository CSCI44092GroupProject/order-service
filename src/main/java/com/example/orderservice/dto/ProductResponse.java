package com.example.orderservice.dto;

import java.math.BigDecimal;

/** Product details returned by the hosted Product Service. */
public record ProductResponse(
        Long productId,
        String name,
        BigDecimal unitPrice,
        String description,
        String category,
        Integer stock
) {}