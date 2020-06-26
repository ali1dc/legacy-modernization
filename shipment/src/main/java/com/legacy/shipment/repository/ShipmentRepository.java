package com.legacy.shipment.repository;

import com.legacy.shipment.model.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShipmentRepository extends CrudRepository<Shipment, Long> {

    Iterable<Shipment> findAll();
    Optional<Shipment> findById(Long id);
    Optional<Shipment> findTopByLegacyId(Long id);
}
