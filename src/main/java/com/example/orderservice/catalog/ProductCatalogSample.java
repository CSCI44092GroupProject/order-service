package com.example.orderservice.catalog;

import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.exception.ProductNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class ProductCatalogSample implements ProductCatalog {

    private final Map<String, ProductResponse> products = new LinkedHashMap<>();

    public ProductCatalogSample() {
        seed("PROD-1001", "Wireless Mouse",      new BigDecimal("25.50"));
        seed("PROD-1002", "Mechanical Keyboard", new BigDecimal("79.99"));
        seed("PROD-1003", "USB-C Hub",           new BigDecimal("34.00"));
        seed("PROD-1004", "27-inch Monitor",     new BigDecimal("219.95"));
    }

    private void seed(String id, String name, BigDecimal price) {
        products.put(id, new ProductResponse(id, name, price));
    }

    @Override
    public ProductResponse getProduct(String productId) {
        ProductResponse product = products.get(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        return product;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return List.copyOf(products.values());
    }
}
