package com.legacy.shipment.service;

import com.legacy.shipment.event.OrderEvent;

public interface OrderService {

    void save(OrderEvent event);
}
