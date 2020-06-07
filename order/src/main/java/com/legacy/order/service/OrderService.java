package com.legacy.order.service;

import com.legacy.order.event.OrderEvent;
import com.legacy.order.model.Order;

public interface OrderService {

    void eventHandler(String data);

    void insert(OrderEvent event);
    void update(OrderEvent event);
    void delete(OrderEvent event);
}
