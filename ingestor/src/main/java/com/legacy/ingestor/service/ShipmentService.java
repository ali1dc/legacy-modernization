package com.legacy.ingestor.service;

import com.legacy.ingestor.events.ShipmentEvent;

public interface ShipmentService {

    void save(ShipmentEvent event);
}
