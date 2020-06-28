package com.legacy.ingestor.service;

import com.legacy.ingestor.config.KafkaTopics;
import com.legacy.ingestor.config.OrderStatuses;
import com.legacy.ingestor.dto.Shipment;
import com.legacy.ingestor.events.ShipmentEvent;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyShipment;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void save(ShipmentEvent event) {

        Shipment shipment = event.getAfter();

        Optional<LegacyOrder> optionalLegacyOrder = orderRepository.findById(shipment.getLegacyOrderId());
        optionalLegacyOrder.ifPresent(order -> {
            LegacyShipment legacyShipment = LegacyShipment.builder()
                    .createdBy(modCreatedBy)
                    .shippingDate(event.getTimestamp())
                    .delivered(shipment.getDelivered())
                    .order(order)
                    .build();
            shipmentRepository.save(legacyShipment);
            kafkaTemplate.send(KafkaTopics.SHIPPING,
                    event.getAfter().getId().toString(),
                    legacyShipment.getId().toString());
            orderService.sendOrderStatus(shipment.getOrderId(),
                    shipment.getLegacyOrderId(),
                    OrderStatuses.SHIPPED);
        });
    }
}
