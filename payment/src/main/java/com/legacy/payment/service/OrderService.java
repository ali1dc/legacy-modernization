package com.legacy.payment.service;

import com.legacy.payment.event.OrderEvent;

public interface OrderService {

    void save(OrderEvent event);
}
