package com.example.orderservice.catalog;

import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Primary
@Slf4j
public class ProductCatalogRestClient implements ProductCatalog {

    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;

    public ProductCatalogRestClient(RestTemplate restTemplate,
                                    @Value("${product.service.base-url}") String productServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.productServiceBaseUrl = productServiceBaseUrl;
    }

    @Override
    public ProductResponse getProduct(Long productId) {
        String url = productServiceBaseUrl + "/api/products/" + productId;
        log.info("Fetching product from Product Service: {}", url);

        try {
            ProductResponse product = restTemplate.getForObject(url, ProductResponse.class);
            if (product == null) {
                throw new ProductNotFoundException(String.valueOf(productId));
            }
            return product;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(404)) {
                throw new ProductNotFoundException(String.valueOf(productId));
            }
            log.error("Error fetching product {}: {}", productId, e.getMessage());
            throw new RestClientException("Failed to fetch product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        String url = productServiceBaseUrl + "/api/products";
        log.info("Fetching all products from Product Service: {}", url);

        ProductResponse[] products = restTemplate.getForObject(url, ProductResponse[].class);
        return products == null ? List.of() : List.of(products);
    }
}