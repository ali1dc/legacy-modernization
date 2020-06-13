package com.legacy.payment.repository;

import com.legacy.payment.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Iterable<Order> findAll();
    Optional<Order> findById(Long id);
    Optional<Order> findTopByLegacyId(Long id);
}
