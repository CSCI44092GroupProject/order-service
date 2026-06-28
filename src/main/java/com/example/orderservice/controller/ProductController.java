package com.example.orderservice.controller;

import com.example.orderservice.catalog.ProductCatalog;
import com.example.orderservice.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Exposes the sample product catalog */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Sample product catalog")
public class ProductController {

    private final ProductCatalog productCatalog;

    @GetMapping
    @Operation(summary = "List all sample products")
    public List<ProductResponse> getAllProducts() {
        return productCatalog.getAllProducts();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get a single product by id")
    public ProductResponse getProduct(@PathVariable String productId) {
        return productCatalog.getProduct(productId);
    }
}
