package com.legacy.customer.repository;

import com.legacy.customer.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository <Customer, Long> {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByLegacyId(Long legacyId);
    Optional<Customer> findTopByEmail(String email);
}
