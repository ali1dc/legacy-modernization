package com.legacy.ingestor.service;

import com.legacy.ingestor.events.OrderEvent;
import com.legacy.ingestor.model.LegacyOrder;

public interface OrderService {

    void eventHandler(String data);
    LegacyOrder save(OrderEvent event);
    void delete(OrderEvent event);
}
