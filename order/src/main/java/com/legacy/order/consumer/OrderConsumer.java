package com.legacy.order.consumer;

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
    public java.util.function.Consumer<KStream<String, String>> customers() {

        return input -> input
                .foreach((key, value) -> {
                    customerService.eventHandler(value);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> products() {

        return input -> input
                .foreach((key, value) -> {
                    productService.eventHandler(value);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> orders() {

        return input -> input
                .foreach((key, value) -> {
                    orderService.eventHandler(value);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> orderItems() {

        return input -> input
                .foreach((key, value) -> {
                    orderItemService.eventHandler(value);
                });
    }
}
