package com.example.orderservice.dto;

import java.math.BigDecimal;

/** Subset of product data returned by the Product Service. */
public record ProductResponse(
        String productId,
        String name,
        BigDecimal price
) {}
