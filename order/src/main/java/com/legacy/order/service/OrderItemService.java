package com.legacy.order.service;

import com.legacy.order.event.OrderItemEvent;

public interface OrderItemService {

    void eventHandler(OrderItemEvent event);

    void insert(OrderItemEvent event);
    void update(OrderItemEvent event);
    void update(Long id, Long legacyId);
    void delete(OrderItemEvent event);
}
