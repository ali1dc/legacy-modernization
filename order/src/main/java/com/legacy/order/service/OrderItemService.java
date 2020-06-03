package com.legacy.order.service;

import com.legacy.order.event.OrderItemEvent;
import com.legacy.order.model.OrderItem;

public interface OrderItemService {

    void eventHandler(String data);

    OrderItem insert(OrderItemEvent event);
    OrderItem update(OrderItemEvent event);
    void delete(OrderItemEvent event);
}
