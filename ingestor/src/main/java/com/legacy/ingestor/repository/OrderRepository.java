package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<LegacyOrder, Long> {
}
