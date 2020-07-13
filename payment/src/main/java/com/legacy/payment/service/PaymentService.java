package com.legacy.payment.service;

import com.legacy.payment.event.PaymentEvent;

public interface PaymentService {

    void eventHandler(PaymentEvent event);
    void insert(PaymentEvent event);
    void update(Long id, Long legacyId);
}
