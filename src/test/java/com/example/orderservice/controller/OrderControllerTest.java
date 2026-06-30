package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.exception.ProductNotFoundException;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController - REST API Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderRequest validRequest;
    private OrderResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new CreateOrderRequest("CUST-1001",
                List.of(new OrderItemRequest(3L, 2)));

        mockResponse = new OrderResponse(
                "ORDER-XYZ",
                "CUST-1001",
                List.of(new OrderItemResponse(3L, "Wireless Mouse", 2,
                        new BigDecimal("120.00"), new BigDecimal("240.00"))),
                new BigDecimal("240.00"),
                LocalDateTime.now(),
                "CREATED"
        );
    }

    @Test
    @DisplayName("POST /api/orders - should return 201 Created on success")
    void createOrder_validRequest_returns201() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORDER-XYZ"))
                .andExpect(jsonPath("$.customerId").value("CUST-1001"))
                .andExpect(jsonPath("$.totalPrice").value(240.00))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items[0].productId").value(3))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @DisplayName("POST /api/orders - should return 400 when customerId is missing")
    void createOrder_missingCustomerId_returns400() throws Exception {
        String invalidJson = """
                {
                  "items": [{ "productId": 3, "quantity": 2 }]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders - should return 400 when items list is empty")
    void createOrder_emptyItems_returns400() throws Exception {
        String invalidJson = """
                {
                  "customerId": "CUST-1001",
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders - should return 400 when quantity is zero or negative")
    void createOrder_invalidQuantity_returns400() throws Exception {
        String invalidJson = """
                {
                  "customerId": "CUST-1001",
                  "items": [{ "productId": 3, "quantity": 0 }]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders - should return 404 when product not found")
    void createOrder_productNotFound_returns404() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new ProductNotFoundException("999"));

        String request = """
                {
                  "customerId": "CUST-1001",
                  "items": [{ "productId": 999, "quantity": 1 }]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }
}