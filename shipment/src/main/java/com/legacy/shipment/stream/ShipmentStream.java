package com.legacy.shipment.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.shipment.config.Actions;
import com.legacy.shipment.event.ShipmentEvent;
import com.legacy.shipment.service.ShipmentService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ShipmentStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ShipmentService shipmentService;

    @Bean
    public java.util.function.Consumer<KStream<String, ShipmentEvent>> shipments() {

        return input -> input
                .filter((key, event) -> {
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .foreach((key, event) -> {
                    shipmentService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacyShipmentIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    shipmentService.update(id, legacyId);
                });
    }
}
