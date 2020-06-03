package com.legacy.order.repository;

import com.legacy.order.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    Optional<OrderItem> findById(Long id);
    Optional<OrderItem> findTopByLegacyId(Long legacyId);
    Optional<List<OrderItem>> findByOrder(Long orderId);
}
