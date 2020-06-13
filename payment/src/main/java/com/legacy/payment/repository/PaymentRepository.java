package com.legacy.payment.repository;

import com.legacy.payment.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Iterable<Payment> findAll();
    Optional<Payment> findById(Long id);
    Optional<Payment> findTopByLegacyId(Long id);
}
