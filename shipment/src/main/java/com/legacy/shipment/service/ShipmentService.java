package com.legacy.shipment.service;

import com.legacy.shipment.event.ShipmentEvent;

public interface ShipmentService {

    void eventHandler(ShipmentEvent event);
    void insert(ShipmentEvent event);
    void update(Long id, Long legacyId);
}
