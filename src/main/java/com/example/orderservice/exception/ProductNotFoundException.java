package com.example.orderservice.exception;

/** Thrown when the requested product does not exist. */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId);
    }
}
