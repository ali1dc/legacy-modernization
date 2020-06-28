package com.legacy.ingestor.service;

import com.legacy.ingestor.dto.OrderStatus;
import com.legacy.ingestor.events.OrderEvent;

public interface OrderService {

    void insert(OrderEvent event);
    void update(OrderEvent event);
    void update(OrderStatus status);
    void sendOrderStatus(Long orderId, Long legacyOrderId, String status);
    void delete(OrderEvent event);
}
