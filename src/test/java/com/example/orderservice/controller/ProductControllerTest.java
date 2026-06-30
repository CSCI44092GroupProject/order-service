package com.example.orderservice.controller;

import com.example.orderservice.catalog.ProductCatalog;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController - REST API Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCatalog productCatalog;

    @Test
    @DisplayName("GET /api/products - should return all products")
    void getAllProducts_returnsList() throws Exception {
        when(productCatalog.getAllProducts()).thenReturn(List.of(
                new ProductResponse(1L, "Mouse", new BigDecimal("25.50"),
                        "desc", "Electronics", 100),
                new ProductResponse(2L, "Keyboard", new BigDecimal("79.99"),
                        "desc", "Electronics", 50)
        ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Mouse"))
                .andExpect(jsonPath("$[1].name").value("Keyboard"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - should return product")
    void getProductById_returnsProduct() throws Exception {
        when(productCatalog.getProduct(3L)).thenReturn(
                new ProductResponse(3L, "USB Hub", new BigDecimal("34.00"),
                        "desc", "Accessories", 200));

        mockMvc.perform(get("/api/products/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(3))
                .andExpect(jsonPath("$.name").value("USB Hub"))
                .andExpect(jsonPath("$.unitPrice").value(34.00));
    }

    @Test
    @DisplayName("GET /api/products/{id} - should return 404 when not found")
    void getProductById_notFound_returns404() throws Exception {
        when(productCatalog.getProduct(999L))
                .thenThrow(new ProductNotFoundException("999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}