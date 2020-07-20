package com.legacy.ingestor.simulation;

import com.legacy.ingestor.config.OrderStatuses;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyShipment;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
public class ShipmentSimulator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String createdBy = "random-generator";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Value("#{new Boolean('${shipment-simulator-enabled}')}")
    private Boolean simulatorEnabled;

    @Scheduled(fixedRate = 4000)
    public void createShipment() {

        if(!simulatorEnabled) return;

        logger.info("creating a random shipment");
        Optional<LegacyOrder> optionalOrder = orderRepository.findRandomCharged();
        optionalOrder.ifPresent(order -> {
            order.setStatus(OrderStatuses.SHIPPED);
            orderRepository.save(order);
            LegacyShipment shipment = LegacyShipment.builder()
                    .order(order)
                    .shippingDate(new Timestamp(System.currentTimeMillis()))
                    .delivered(false)
                    .createdBy(createdBy)
                    .build();
            shipmentRepository.save(shipment);
        });
    }

    @Scheduled(fixedRate = 5000)
    public void setDelivered() {

        if(!simulatorEnabled) return;

        logger.info("creating a random shipment");
        Optional<LegacyOrder> optionalOrder = orderRepository.findRandomShipped();
        optionalOrder.ifPresent(order -> {
            Optional<LegacyShipment> optionalShipment = shipmentRepository.findTopByOrderId(order.getId());
            optionalShipment.ifPresent(shipment -> {
                order.setStatus(OrderStatuses.DELIVERED);
                orderRepository.save(order);
                shipment.setDelivered(true);
                shipmentRepository.save(shipment);
            });
        });
    }
}
