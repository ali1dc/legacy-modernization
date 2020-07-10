package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyShipment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShipmentRepository extends CrudRepository<LegacyShipment, Long> {

    Optional<LegacyShipment> findTopByOrderOrderId(Long orderId);
}
