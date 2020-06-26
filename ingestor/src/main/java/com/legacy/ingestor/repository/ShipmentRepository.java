package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyShipment;
import org.springframework.data.repository.CrudRepository;

public interface ShipmentRepository extends CrudRepository<LegacyShipment, Long> {
}
