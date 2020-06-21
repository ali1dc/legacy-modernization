package com.legacy.ingestor.service;

import com.legacy.ingestor.events.OrderItemEvent;
import com.legacy.ingestor.model.LegacyOrderItem;

public interface OrderItemService {

    void eventHandler(OrderItemEvent data);
    LegacyOrderItem save(OrderItemEvent event);
    void delete(OrderItemEvent event);
}
