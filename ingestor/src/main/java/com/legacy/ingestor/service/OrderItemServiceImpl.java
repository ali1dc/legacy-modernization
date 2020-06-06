package com.legacy.ingestor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.*;
import com.legacy.ingestor.events.OrderItemEvent;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyOrderItem;
import com.legacy.ingestor.model.LegacyProduct;
import com.legacy.ingestor.repository.OrderItemRepository;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

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
    private InteractiveQueryService interactiveQueryService;

    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(String data) {

        JsonNode jsonNode;
        OrderItemEvent event;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), OrderItemEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

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
    public LegacyOrderItem save(OrderItemEvent event) {

        OrderItem orderItem = event.getAfter();
        if (orderItem.getLegacyId() != null) return orderItemRepository.findById(orderItem.getLegacyId()).get();

        ReadOnlyKeyValueStore<Long, Customer> customerStore =
                interactiveQueryService.getQueryableStore(StateStores.CUSTOMER_STORE, QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<Long, Product> productStore =
                interactiveQueryService.getQueryableStore(StateStores.PRODUCT_STORE, QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<Long, Order> orderStore =
                interactiveQueryService.getQueryableStore(StateStores.ORDER_STORE, QueryableStoreTypes.keyValueStore());

        Order order = orderStore.get(orderItem.getOrderId());
        Product product = productStore.get(orderItem.getProductId());
        Customer customer = customerStore.get(order.getCustomerId());
        LegacyOrder legacyOrder = orderRepository.findById(order.getLegacyId()).get();
        LegacyProduct legacyProduct = productRepository.findById(product.getLegacyId()).get();

        LegacyOrderItem legacyOrderItem = LegacyOrderItem.builder()
                .order(legacyOrder)
                .product(legacyProduct)
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .createdBy(modCreatedBy)
                .build();
        orderItemRepository.save(legacyOrderItem);

        return legacyOrderItem;
    }

    @Override
    public void delete(OrderItemEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
