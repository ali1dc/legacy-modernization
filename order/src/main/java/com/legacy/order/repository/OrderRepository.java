package com.legacy.order.repository;

import com.legacy.order.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Iterable<Order> findAll();
    Optional<Order> findById(Long id);
    Optional<Order> findTopByLegacyId(Long legacyId);
    Optional<List<Order>> findByStatus(String status);
}
