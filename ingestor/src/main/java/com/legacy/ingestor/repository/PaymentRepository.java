package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyPayment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<LegacyPayment, Long> {
}
