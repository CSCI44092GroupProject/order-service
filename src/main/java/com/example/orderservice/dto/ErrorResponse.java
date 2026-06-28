package com.example.orderservice.dto;

import java.time.LocalDateTime;

/** Standard error body returned by the global exception handler. */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}
