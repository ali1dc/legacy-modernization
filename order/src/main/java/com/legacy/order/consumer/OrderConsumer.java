package com.legacy.order.consumer;

import com.legacy.order.dto.OrderStatus;
import com.legacy.order.event.*;
import com.legacy.order.service.CustomerService;
import com.legacy.order.service.OrderItemService;
import com.legacy.order.service.OrderService;
import com.legacy.order.service.ProductService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @Bean
    public java.util.function.Consumer<KStream<String, CustomerEvent>> oCustomers() {

        return input -> input
                .foreach((key, event) -> {
                    customerService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, ProductEvent>> oProducts() {

        return input -> input
                .foreach((key, event) -> {
                    productService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, OrderEvent>> orders1() {

        return input -> input
                .foreach((key, event) -> {
                    orderService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, OrderItemEvent>> orderItems() {

        return input -> input
                .foreach((key, event) -> {
                    orderItemService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacyOrderIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    orderService.update(id, legacyId);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacyOrderItemIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    orderItemService.update(id, legacyId);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, OrderStatus>> orderStatus() {

        return os -> os.foreach((key, status) -> orderService.update(status));
    }
}
