package com.example.orderservice.catalog;

import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCatalogRestClient Tests")
class ProductCatalogRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ProductCatalogRestClient client;

    @BeforeEach
    void setUp() {
        client = new ProductCatalogRestClient(restTemplate, "http://fake-url");
    }

    @Test
    @DisplayName("Should fetch product from Product Service")
    void getProduct_success() {
        ProductResponse mockProduct = new ProductResponse(3L, "Wireless Mouse",
                new BigDecimal("120.00"), "desc", "Electronics", 100);
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenReturn(mockProduct);

        ProductResponse result = client.getProduct(3L);

        assertThat(result).isNotNull();
        assertThat(result.productId()).isEqualTo(3L);
        assertThat(result.name()).isEqualTo("Wireless Mouse");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when 404 received")
    void getProduct_notFound_throwsException() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getProduct(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when null response")
    void getProduct_nullResponse_throwsException() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> client.getProduct(123L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Should fetch all products")
    void getAllProducts_success() {
        ProductResponse[] mockProducts = {
                new ProductResponse(1L, "A", new BigDecimal("10"), "d", "c", 5),
                new ProductResponse(2L, "B", new BigDecimal("20"), "d", "c", 5)
        };
        when(restTemplate.getForObject(anyString(), eq(ProductResponse[].class)))
                .thenReturn(mockProducts);

        var products = client.getAllProducts();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).name()).isEqualTo("A");
    }

    @Test
    @DisplayName("Should return empty list when API returns null")
    void getAllProducts_nullResponse_returnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse[].class)))
                .thenReturn(null);

        var products = client.getAllProducts();

        assertThat(products).isEmpty();
    }
}