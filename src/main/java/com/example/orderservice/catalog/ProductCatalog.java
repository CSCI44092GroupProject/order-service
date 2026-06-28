package com.example.orderservice.catalog;

import com.example.orderservice.dto.ProductResponse;

import java.util.List;


public interface ProductCatalog {

    /** @return the product, or throws ProductNotFoundException if not exists. */
    ProductResponse getProduct(Long productId);

    /** @return all available products */
    List<ProductResponse> getAllProducts();
}
