package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<LegacyOrder, Long> {

    @Query(value = "SELECT order_id, customer_id, status, order_date, created_by " +
            "FROM orders WHERE status='pending' " +
            "ORDER BY rand() LIMIT 1", nativeQuery = true)
    Optional<LegacyOrder> findRandomPending();

    @Query(value = "SELECT order_id, customer_id, status, order_date, created_by " +
            "FROM orders WHERE status='charged' " +
            "ORDER BY rand() LIMIT 1", nativeQuery = true)
    Optional<LegacyOrder> findRandomCharged();

    @Query(value = "SELECT order_id, customer_id, status, order_date, created_by " +
            "FROM orders WHERE status='shipped' " +
            "ORDER BY rand() LIMIT 1", nativeQuery = true)
    Optional<LegacyOrder> findRandomShipped();
}
