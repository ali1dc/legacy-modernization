package com.legacy.shipment.repository;

import com.legacy.shipment.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Iterable<Order> findAll();
    Optional<Order> findById(Long id);
    Optional<Order> findTopByLegacyId(Long id);
}
