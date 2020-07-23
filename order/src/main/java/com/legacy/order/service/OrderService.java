package com.legacy.order.service;

import com.legacy.order.event.OrderEvent;
import com.legacy.order.dto.OrderStatus;

public interface OrderService {

    void eventHandler(OrderEvent event);

    void insert(OrderEvent event);
    void update(OrderEvent event);
    void update(Long id, Long legacyId);
    void update(OrderStatus status);
    void delete(OrderEvent event);
}
