package com.legacy.payment.service;

import com.legacy.payment.event.PaymentEvent;

public interface PaymentService {

    void eventHandler(PaymentEvent data);
    void insert(PaymentEvent event);
    void update(Long id, Long legacyId);
}
