package com.legacy.customer.repository;

import com.legacy.customer.model.CustomerAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends CrudRepository<CustomerAddress, Long> {
    Optional<CustomerAddress> findById(Long id);
    Optional<List<CustomerAddress>> findByCustomerId(Long id);
}
