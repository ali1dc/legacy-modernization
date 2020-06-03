package com.legacy.order.controller;

import com.legacy.order.model.Customer;
import com.legacy.order.model.Order;
import com.legacy.order.model.OrderItem;
import com.legacy.order.model.Product;
import com.legacy.order.repository.CustomerRepository;
import com.legacy.order.repository.OrderItemRepository;
import com.legacy.order.repository.OrderRepository;
import com.legacy.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;


    @GetMapping("/customers")
    public List<Customer> customers() {

        logger.info("returning all customers");
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(customers::add);
        return customers;
    }

    @GetMapping("/customers/{id}")
    public Customer customer(@PathVariable("id") Long id) {

        logger.info("returning customer: {}", id);
        return customerRepository.findById(id).orElseGet(Customer::new);
    }

    @GetMapping("/products")
    public List<Product> products() {

        logger.info("returning all products");
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    @GetMapping("/products/{id}")
    public Product product(@PathVariable("id") Long id) {

        logger.info("returning product: {}", id);
        return productRepository.findById(id).orElseGet(Product::new);
    }

    @GetMapping("/orders")
    public List<Order> orders() {

        logger.info("returning all products");
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    @GetMapping("/orders/{id}")
    public Order order(@PathVariable("id") Long id) {

        logger.info("returning order: {}", id);
        return orderRepository.findById(id).orElseGet(Order::new);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Order> status(@RequestParam(value="status") String status) {

        logger.info("returning all orders with status: {}", status);
        Optional<List<Order>>  optionalOrders = orderRepository.findByStatus(status);
        return optionalOrders.orElseGet(ArrayList::new);
    }

    @GetMapping("/order-items/{id}")
    public List<OrderItem> orderItems(@PathVariable("id") Long id) {

        logger.info("returning order item for order: {}", id);
        return orderItemRepository.findByOrder(id).orElseGet(ArrayList::new);
    }
}
