package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** One requested product */
@Schema(description = "A product line in a new order")
public record OrderItemRequest(

        @Schema(example = "PROD-1002")
        @NotBlank(message = "productId is required")
        String productId,

        @Schema(example = "2")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity
) {}