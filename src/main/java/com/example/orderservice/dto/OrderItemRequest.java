package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** One requested product line. */
@Schema(description = "A product line in a new order")
public record OrderItemRequest(

        @Schema(example = "3")
        @NotNull(message = "productId is required")
        Long productId,

        @Schema(example = "2")
        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity
) {}