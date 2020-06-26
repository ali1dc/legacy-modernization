package com.legacy.shipment.service;

import com.legacy.shipment.event.OrderEvent;
import com.legacy.shipment.model.Customer;
import com.legacy.shipment.model.Order;
import com.legacy.shipment.repository.CustomerRepository;
import com.legacy.shipment.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

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
}
