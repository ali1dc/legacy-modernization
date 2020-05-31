package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyCustomer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<LegacyCustomer, Long> {

    Optional<LegacyCustomer> findById(Long id);
    Optional<LegacyCustomer> findTopByEmail(String Email);
}
