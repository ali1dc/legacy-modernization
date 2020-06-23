package com.legacy.payment.repository;

import com.legacy.payment.model.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Iterable<Payment> findAll();
    Optional<Payment> findById(Long id);
    Optional<Payment> findTopByLegacyId(Long id);
}
