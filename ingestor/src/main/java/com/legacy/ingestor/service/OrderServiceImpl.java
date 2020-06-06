package com.legacy.ingestor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Customer;
import com.legacy.ingestor.dto.Order;
import com.legacy.ingestor.events.OrderEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.repository.CustomerRepository;
import com.legacy.ingestor.repository.OrderRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(String data) {

        JsonNode jsonNode;
        OrderEvent event;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), OrderEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        switch (event.getOp()) {
            case Actions.CREATE:
                save(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public LegacyOrder save(OrderEvent event) {

        logger.info("inserting/updating the order record");

        Order order = event.getAfter();
        ReadOnlyKeyValueStore<Long, Customer> customerStore =
                interactiveQueryService.getQueryableStore(StateStores.CUSTOMER_STORE, QueryableStoreTypes.keyValueStore());

        Customer customer = customerStore.get(order.getCustomerId());
        LegacyCustomer legacyCustomer = customerRepository.findById(customer.getLegacyId()).get();

        if (order.getLegacyId() != null) {
            Optional<LegacyOrder> optionalLegacyOrder = orderRepository.findById(order.getLegacyId());
            return optionalLegacyOrder.get();
        }

        LegacyOrder legacyOrder = LegacyOrder.builder()
                .status(order.getStatus())
                .createdBy(modCreatedBy)
                .orderDate(event.getTimestamp())
                .customer(legacyCustomer)
                .build();
        orderRepository.save(legacyOrder);

        return legacyOrder;
    }

    @Override
    public void delete(OrderEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
