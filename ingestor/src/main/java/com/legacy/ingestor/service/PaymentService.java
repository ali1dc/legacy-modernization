package com.legacy.ingestor.service;

import com.legacy.ingestor.events.PaymentEvent;
import com.legacy.ingestor.model.LegacyPayment;

public interface PaymentService {

    void save(PaymentEvent event);
}
