package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyCustomer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<LegacyCustomer, Long> {

    Optional<LegacyCustomer> findById(Long id);
    Optional<LegacyCustomer> findTopByEmail(String Email);
    @Query(value = "SELECT TOP 1 id, first_name, last_name, billing_address1, billing_address2, billing_city, billing_state, " +
            "billing_zip, shipping_address1, shipping_address2, shipping_city, shipping_state, shipping_zip, email, " +
            "phone, created_by " +
            "FROM customers ORDER BY NEWID()", nativeQuery = true)
    Optional<LegacyCustomer> findRandom();
}
