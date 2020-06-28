package com.legacy.ingestor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.KafkaTopics;
import com.legacy.ingestor.dto.*;
import com.legacy.ingestor.events.OrderItemEvent;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyOrderItem;
import com.legacy.ingestor.model.LegacyProduct;
import com.legacy.ingestor.repository.OrderItemRepository;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(OrderItemEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
                save(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public void save(OrderItemEvent event) {

        OrderItem orderItem = event.getAfter();
        if (orderItem.getLegacyId() != null) return;

        Optional<LegacyOrder> optionalLegacyOrder = orderRepository.findById(orderItem.getLegacyOrderId());
        Optional<LegacyProduct> optionalLegacyProduct = productRepository.findById(orderItem.getLegacyProductId());

        if (optionalLegacyOrder.isPresent() && optionalLegacyProduct.isPresent()) {
            LegacyOrderItem legacyOrderItem = LegacyOrderItem.builder()
                    .order(optionalLegacyOrder.get())
                    .product(optionalLegacyProduct.get())
                    .quantity(orderItem.getQuantity())
                    .unitPrice(orderItem.getUnitPrice())
                    .createdBy(modCreatedBy)
                    .build();
            orderItemRepository.save(legacyOrderItem);
            if (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ)) {
                kafkaTemplate.send(KafkaTopics.ORDER_ITEM,
                        event.getAfter().getId().toString(),
                        legacyOrderItem.getItemId().toString());
            }
        }
    }

    @Override
    public void delete(OrderItemEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
