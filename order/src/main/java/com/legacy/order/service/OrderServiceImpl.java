package com.legacy.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.OrderEvent;
import com.legacy.order.dto.OrderStatus;
import com.legacy.order.model.Order;
import com.legacy.order.repository.CustomerRepository;
import com.legacy.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
    public void eventHandler(OrderEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
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
        order.setLegacyCustomerId(event.getAfter().getCustomerId());

        // insert if customer exists
        customerRepository.findTopByLegacyId(order.getCustomerId()).ifPresent(customer -> {
            order.setCustomer(customer);
            orderRepository.save(order);
        });
    }

    @Override
    public void update(OrderEvent event) {

        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(event.getAfter().getId());
        if (!optionalOrder.isPresent()) return;

        Order order = optionalOrder.get();
        // only update is status changed
        if (Objects.equals(order.getStatus(), event.getAfter().getStatus())) return;

        order.setStatus(event.getAfter().getStatus());
        order.setUpdatedBy(legacyCreatedBy);
        order.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        // handle customer id
        customerRepository.findTopByLegacyId(event.getAfter().getCustomerId()).ifPresent(customer -> {
            order.setCustomer(customer);
            orderRepository.save(order);
        });
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getLegacyId() == null) {
                order.setLegacyId(legacyId);
                order.setUpdatedBy(legacyCreatedBy);
                order.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                orderRepository.save(order);
            }
        });
    }

    @Override
    public void update(OrderStatus status) {

        logger.info("updating order status!");
        Optional<Order> optionalOrder = orderRepository.findById(status.getOrderId());
        optionalOrder.ifPresent(order -> {
            if (!Objects.equals(order.getStatus(), status.getStatus())) {
                order.setStatus(status.getStatus());
                order.setUpdatedBy(modCreatedBy);
                order.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                orderRepository.save(order);
            }
        });
    }

    @Override
    public void delete(OrderEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
