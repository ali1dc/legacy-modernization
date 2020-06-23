package com.legacy.payment.repository;

import com.legacy.payment.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findById(Long Id);
    Optional<Customer> findTopByLegacyId(Long legacyId);
}
