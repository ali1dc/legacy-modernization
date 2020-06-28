package com.legacy.shipment.service;

import com.legacy.shipment.dto.OrderStatus;
import com.legacy.shipment.event.OrderEvent;

public interface OrderService {

    void save(OrderEvent event);
    void update(OrderStatus status);
}
