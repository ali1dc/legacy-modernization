package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyOrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends CrudRepository<LegacyOrderItem, Long> {

    Optional<List<LegacyOrderItem>> findAllByOrderOrderId(Long orderId);
}
