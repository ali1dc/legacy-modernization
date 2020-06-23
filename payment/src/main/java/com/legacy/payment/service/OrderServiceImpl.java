package com.legacy.payment.service;

import com.legacy.payment.event.OrderEvent;
import com.legacy.payment.model.Customer;
import com.legacy.payment.model.Order;
import com.legacy.payment.repository.CustomerRepository;
import com.legacy.payment.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

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
    }
}
