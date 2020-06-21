package com.legacy.order.service;

import com.legacy.order.event.OrderEvent;

public interface OrderService {

    void eventHandler(String data);

    void insert(OrderEvent event);
    void update(OrderEvent event);
    void update(Long id, Long legacyId);
    void delete(OrderEvent event);
}
