package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Request to create an order containing one or more products. */
@Schema(description = "Request to create a new order")
public record CreateOrderRequest(

        @Schema(example = "CUST-1001")
        @NotBlank(message = "customerId is required")
        String customerId,

        @NotEmpty(message = "at least one item is required")
        List<@NotNull @Valid OrderItemRequest> items
) {}
