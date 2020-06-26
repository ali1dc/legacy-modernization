package com.legacy.ingestor.service;

import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.LegacyIdTopics;
import com.legacy.ingestor.dto.Order;
import com.legacy.ingestor.events.OrderEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.repository.CustomerRepository;
import com.legacy.ingestor.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void insert(OrderEvent event) {

        logger.info("inserting/updating the order record");
        Order order = event.getAfter();
        if (order.getLegacyId() != null) return;

        Optional<LegacyCustomer> optionalLegacyCustomer = customerRepository.findById(order.getLegacyCustomerId());
        optionalLegacyCustomer.ifPresent(customer -> {
            LegacyOrder legacyOrder = LegacyOrder.builder()
                    .status(order.getStatus())
                    .createdBy(modCreatedBy)
                    .orderDate(event.getTimestamp())
                    .customer(customer)
                    .build();
            orderRepository.save(legacyOrder);
            if (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ)) {
                kafkaTemplate.send(LegacyIdTopics.ORDER,
                        event.getAfter().getId().toString(),
                        legacyOrder.getOrderId().toString());
            }
        });
    }

    @Override
    public void update(OrderEvent event) {
        logger.info("updating order status when changed");
    }

    @Override
    public void delete(OrderEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
