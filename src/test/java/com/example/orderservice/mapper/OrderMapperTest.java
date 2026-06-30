package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderMapper Tests")
class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    @DisplayName("Should map Order entity to OrderResponse DTO")
    void toResponse_mapsCorrectly() {
        Order order = new Order();
        order.setOrderId("ORDER-123");
        order.setCustomerId("CUST-001");
        order.setTotalPrice(new BigDecimal("250.00"));
        order.setOrderDate(LocalDateTime.of(2026, 6, 29, 10, 30));
        order.setStatus(OrderStatus.CREATED);

        OrderItem item = OrderItem.builder()
                .orderItemId("ITEM-1")
                .productId(3L)
                .productName("Wireless Mouse")
                .quantity(2)
                .unitPrice(new BigDecimal("125.00"))
                .lineTotal(new BigDecimal("250.00"))
                .build();
        order.addItem(item);

        OrderResponse response = mapper.toResponse(order);

        assertThat(response.orderId()).isEqualTo("ORDER-123");
        assertThat(response.customerId()).isEqualTo("CUST-001");
        assertThat(response.totalPrice()).isEqualByComparingTo("250.00");
        assertThat(response.status()).isEqualTo("CREATED");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productName()).isEqualTo("Wireless Mouse");
        assertThat(response.items().get(0).lineTotal()).isEqualByComparingTo("250.00");
    }

    @Test
    @DisplayName("Should handle order with no items")
    void toResponse_emptyItems() {
        Order order = new Order();
        order.setOrderId("ORDER-456");
        order.setCustomerId("CUST-002");
        order.setTotalPrice(BigDecimal.ZERO);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        OrderResponse response = mapper.toResponse(order);

        assertThat(response.items()).isEmpty();
        assertThat(response.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}