package com.legacy.ingestor.service;

import com.legacy.ingestor.events.PaymentEvent;

public interface PaymentService {

    void save(PaymentEvent event);
}
