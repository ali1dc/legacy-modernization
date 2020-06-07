package com.legacy.order.service;

import com.legacy.order.event.OrderItemEvent;

public interface OrderItemService {

    void eventHandler(String data);

    void insert(OrderItemEvent event);
    void update(OrderItemEvent event);
    void delete(OrderItemEvent event);
}
