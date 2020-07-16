package com.legacy.ingestor.service;

import com.legacy.ingestor.dto.Outbox;
import com.legacy.ingestor.events.CustomerEvent;

public interface CustomerService {

    void handler(CustomerEvent event);
    void insert(Outbox outbox);
}
