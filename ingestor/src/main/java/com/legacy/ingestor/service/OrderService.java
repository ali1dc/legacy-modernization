package com.legacy.ingestor.service;

import com.legacy.ingestor.dto.Customer;
import com.legacy.ingestor.dto.Order;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.events.OrderEvent;
import com.legacy.ingestor.model.LegacyOrder;

public interface OrderService {

    LegacyOrder save(OrderEvent event);
    void update(OrderEvent event);
    void delete(OrderEvent event);
}
