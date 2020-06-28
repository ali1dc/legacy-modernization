package com.legacy.shipment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.shipment.config.Actions;
import com.legacy.shipment.config.KafkaTopics;
import com.legacy.shipment.config.OrderStatuses;
import com.legacy.shipment.dto.OrderStatus;
import com.legacy.shipment.event.ShipmentEvent;
import com.legacy.shipment.model.Order;
import com.legacy.shipment.model.Shipment;
import com.legacy.shipment.repository.CustomerRepository;
import com.legacy.shipment.repository.OrderRepository;
import com.legacy.shipment.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(ShipmentEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
                insert(event);
                break;
        }
    }

    @Override
    public void insert(ShipmentEvent event) {

        Shipment shipment = event.getAfter();
        if (Objects.equals(shipment.getCreatedBy(), modCreatedBy)) return;

        Optional<Shipment> optionalShipment = shipmentRepository.findTopByLegacyId(shipment.getId());
        if(optionalShipment.isPresent()) {
            logger.info("duplicate shipment, no need to insert the record!");
            return;
        }
        shipment.setLegacyId(shipment.getId());
        shipment.setLegacyOrderId(shipment.getOrderId());
        shipment.setCreatedBy(legacyCreatedBy);
        shipment.setCreatedDate(event.getTimestamp());
        shipment.setId(null);

        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(shipment.getOrderId());

        optionalOrder.ifPresent(order -> {
            shipment.setOrder(order);
            shipmentRepository.save(shipment);
            sendStatus(shipment);
        });
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<Shipment> optionalShipment = shipmentRepository.findById(id);
        optionalShipment.ifPresent(shipment -> {
            if (shipment.getLegacyId() == null) {
                shipment.setLegacyId(legacyId);
                shipment.setUpdatedBy(legacyCreatedBy);
                shipment.setUpdatedDate(new Date());
                shipmentRepository.save(shipment);
            }
        });
    }

    private void sendStatus(Shipment shipment) {

        OrderStatus status = OrderStatus.builder()
                .orderId(shipment.getOrder().getId())
                .Status(OrderStatuses.SHIPPED)
                .legacyOrderId(shipment.getLegacyOrderId())
                .build();
        try {
            kafkaTemplate.send(KafkaTopics.ORDER_STATUS_TOPIC,
                    status.getOrderId().toString(),
                    jsonMapper.writeValueAsString(status));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }
}
