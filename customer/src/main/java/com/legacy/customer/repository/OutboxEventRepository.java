package com.legacy.customer.repository;

import com.legacy.customer.model.OutboxEvent;
import org.springframework.data.repository.CrudRepository;

public interface OutboxEventRepository extends CrudRepository<OutboxEvent, Long> {
}
