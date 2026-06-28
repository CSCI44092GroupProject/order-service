package com.example.orderservice.service;

import com.example.orderservice.catalog.ProductCatalog;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order creation workflow:
 *   1. for each requested item, look up the product in the sample catalog
 *   2. compute that product's total (unit price x quantity)
 *   3. sum all products into the order's grand total
 *   4. persist the order with its items
 *   5. log the order-created event
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductCatalog productCatalog;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            ProductResponse product = productCatalog.getProduct(itemReq.productId());
            BigDecimal lineTotal = product.price()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = OrderItem.builder()
                    .productId(product.productId())
                    .productName(product.name())
                    .quantity(itemReq.quantity())
                    .unitPrice(product.price())
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(item);
            grandTotal = grandTotal.add(lineTotal);
        }

        order.setTotalPrice(grandTotal);
        Order saved = orderRepository.save(order);

        log.info("ORDER CREATED EVENT -> orderId={}, customerId={}, items={}, total={}",
                saved.getOrderId(), saved.getCustomerId(), saved.getItems().size(), saved.getTotalPrice());

        return mapper.toResponse(saved);
    }
}