package com.example.orderservice.service;

import com.example.orderservice.catalog.ProductCatalog;
import com.example.orderservice.dto.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.ProductNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl - Business Logic Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductCatalog productCatalog;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private ProductResponse sampleProduct;
    private CreateOrderRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductResponse(
                3L,
                "Wireless Mouse",
                new BigDecimal("120.00"),
                "Description for Wireless Mouse",
                "Electronics",
                100
        );

        sampleRequest = new CreateOrderRequest(
                "CUST-1001",
                List.of(new OrderItemRequest(3L, 2))
        );
    }

    @Test
    @DisplayName("Should create an order successfully with one item")
    void createOrder_singleItem_success() {
        // Given
        when(productCatalog.getProduct(3L)).thenReturn(sampleProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse expectedResponse = new OrderResponse(
                "ORDER-123",
                "CUST-1001",
                List.of(new OrderItemResponse(3L, "Wireless Mouse", 2,
                        new BigDecimal("120.00"), new BigDecimal("240.00"))),
                new BigDecimal("240.00"),
                LocalDateTime.now(),
                "CREATED"
        );
        when(orderMapper.toResponse(any(Order.class))).thenReturn(expectedResponse);

        // When
        OrderResponse result = orderService.createOrder(sampleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo("CUST-1001");
        assertThat(result.totalPrice()).isEqualByComparingTo("240.00");
        assertThat(result.items()).hasSize(1);

        verify(productCatalog, times(1)).getProduct(3L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should calculate total price correctly for multiple items")
    void createOrder_multipleItems_calculatesTotalCorrectly() {
        // Given
        ProductResponse product1 = new ProductResponse(1L, "Item A",
                new BigDecimal("50.00"), "desc", "cat", 10);
        ProductResponse product2 = new ProductResponse(2L, "Item B",
                new BigDecimal("30.00"), "desc", "cat", 20);

        CreateOrderRequest request = new CreateOrderRequest("CUST-001", List.of(
                new OrderItemRequest(1L, 2),  // 50 × 2 = 100
                new OrderItemRequest(2L, 3)   // 30 × 3 = 90
        ));

        when(productCatalog.getProduct(1L)).thenReturn(product1);
        when(productCatalog.getProduct(2L)).thenReturn(product2);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            return new OrderResponse(o.getOrderId(), o.getCustomerId(),
                    List.of(), o.getTotalPrice(), o.getOrderDate(), o.getStatus().name());
        });

        // When
        OrderResponse result = orderService.createOrder(request);

        // Then — total should be 100 + 90 = 190
        assertThat(result.totalPrice()).isEqualByComparingTo("190.00");

        verify(productCatalog).getProduct(1L);
        verify(productCatalog).getProduct(2L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product doesn't exist")
    void createOrder_productNotFound_throwsException() {
        // Given
        when(productCatalog.getProduct(999L))
                .thenThrow(new ProductNotFoundException("999"));

        CreateOrderRequest request = new CreateOrderRequest("CUST-001",
                List.of(new OrderItemRequest(999L, 1)));

        // When + Then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should set order status to CREATED")
    void createOrder_setsStatusToCreated() {
        // Given
        when(productCatalog.getProduct(3L)).thenReturn(sampleProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order savedOrder = inv.getArgument(0);
            // Verify the status was set to CREATED before saving
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
            return savedOrder;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mock(OrderResponse.class));

        // When
        orderService.createOrder(sampleRequest);

        // Then
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should set order date when creating an order")
    void createOrder_setsOrderDate() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        when(productCatalog.getProduct(3L)).thenReturn(sampleProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order savedOrder = inv.getArgument(0);
            assertThat(savedOrder.getOrderDate()).isNotNull();
            assertThat(savedOrder.getOrderDate()).isAfterOrEqualTo(before);
            return savedOrder;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mock(OrderResponse.class));

        // When
        orderService.createOrder(sampleRequest);

        // Then
        verify(orderRepository).save(any(Order.class));
    }
}