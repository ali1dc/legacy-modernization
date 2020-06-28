package com.legacy.shipment.service;

import com.legacy.shipment.dto.OrderStatus;
import com.legacy.shipment.event.OrderEvent;
import com.legacy.shipment.model.Customer;
import com.legacy.shipment.model.Order;
import com.legacy.shipment.repository.CustomerRepository;
import com.legacy.shipment.repository.OrderRepository;
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
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void save(OrderEvent event) {

        Optional<Customer> optionalCustomer = customerRepository.findById(event.getAfter().getCustomerId());
        optionalCustomer.ifPresent(customer -> {
            Order order = event.getAfter();
            order.setCustomer(customer);
            orderRepository.save(order);
        });
        logger.info("order saved successfully!");
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
}
