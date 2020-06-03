package com.legacy.order.service;

import com.legacy.order.event.OrderEvent;
import com.legacy.order.model.Order;

public interface OrderService {

    void eventHandler(String data);

    Order insert(OrderEvent event);
    Order update(OrderEvent event);
    void delete(OrderEvent event);
}
