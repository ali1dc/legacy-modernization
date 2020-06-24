package com.legacy.ingestor.service;

import com.legacy.ingestor.events.OrderEvent;

public interface OrderService {

    void insert(OrderEvent event);
    void update(OrderEvent event);
    void delete(OrderEvent event);
}
