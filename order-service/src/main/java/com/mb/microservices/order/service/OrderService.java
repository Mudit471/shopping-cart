package com.mb.microservices.order.service;

import com.mb.microservices.order.client.InventoryClient;
import com.mb.microservices.order.dto.OrderRequest;
import com.mb.microservices.order.dto.OrderResponse;
import com.mb.microservices.order.event.OrderPlacedEvent;
import com.mb.microservices.order.model.Order;
import com.mb.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (!isProductInStock) {
            throw new RuntimeException("Product with skuCode " + orderRequest.skuCode() + " is not in stock");
        }
        Order order = Order.builder().orderNumber(UUID.randomUUID().toString())
                .skuCode(orderRequest.skuCode()).price(orderRequest.price())
                .quantity(orderRequest.quantity()).build();
        orderRepository.save(order);
        // Send the message to Kafka Topic
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber());
        log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
        kafkaTemplate.send("order-placed", orderPlacedEvent);
        log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
        new OrderResponse(order.getId(), order.getOrderNumber(),
                order.getSkuCode(), order.getPrice(), order.getQuantity());
    }
}

