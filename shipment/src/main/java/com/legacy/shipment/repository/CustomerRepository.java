package com.legacy.shipment.repository;

import com.legacy.shipment.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findById(Long Id);
    Optional<Customer> findTopByLegacyId(Long legacyId);
}
