package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyOrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<LegacyOrderItem, Long> {
}
