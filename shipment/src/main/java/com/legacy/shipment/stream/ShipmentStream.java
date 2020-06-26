package com.legacy.shipment.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    public java.util.function.Consumer<KStream<String, String>> shipments() {

        return input -> input
                .filter((key, value) -> {
                    ShipmentEvent event = getShipmentEvent(value);
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .foreach((key, value) -> {
                    ShipmentEvent event = getShipmentEvent(value);
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

    private ShipmentEvent getShipmentEvent(String data) {

        JsonNode jsonNode;
        ShipmentEvent event = null;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            Integer isDelivered = jsonNode.at("/after/delivered").asInt();
            event = jsonMapper.readValue(jsonNode.toString(), ShipmentEvent.class);
            event.getAfter().setDelivered(isDelivered == 1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return event;
    }
}
