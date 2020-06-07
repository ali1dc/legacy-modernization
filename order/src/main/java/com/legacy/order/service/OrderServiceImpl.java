package com.legacy.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.OrderEvent;
import com.legacy.order.model.Customer;
import com.legacy.order.model.Order;
import com.legacy.order.repository.CustomerRepository;
import com.legacy.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
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

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
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
                insert(event);
                break;
            case Actions.UPDATE:
                update(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public void insert(OrderEvent event) {

        Order order = event.getAfter();
        if (Objects.equals(order.getCreatedBy(), modCreatedBy)) return;
        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(order.getId());
        if (optionalOrder.isPresent()) {
            logger.info("duplicate order, no need to insert the order!");
            return;
        }
        order.setLegacyId(order.getId());
        order.setId(null);
        order.setCreatedBy(legacyCreatedBy);

        // handle customer
        Customer customer = customerRepository.findTopByLegacyId(event.getAfter().getCustomerId()).get();
        order.setCustomer(customer);

        orderRepository.save(order);
    }

    @Override
    public void update(OrderEvent event) {

        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(event.getAfter().getId());
        if (!optionalOrder.isPresent()) return;

        Order order = optionalOrder.get();
        order.setStatus(event.getAfter().getStatus());
        order.setUpdatedBy(legacyCreatedBy);
        order.setUpdatedDate(new Date());
        // handle customer id
        Customer customer = customerRepository.findTopByLegacyId(event.getAfter().getCustomerId()).get();

        order.setCustomerId(customer.getId());
        order.setCustomer(customer);

        orderRepository.save(order);
    }

    @Override
    public void delete(OrderEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
