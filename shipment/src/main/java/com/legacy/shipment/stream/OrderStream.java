package com.legacy.shipment.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.shipment.config.Actions;
import com.legacy.shipment.event.OrderEvent;
import com.legacy.shipment.service.OrderService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderService orderService;

    @Bean
    public java.util.function.Consumer<KStream<String, OrderEvent>> shOrders() {

        return input -> input
                .filter((key, event) -> {
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .foreach((key, event) -> {
                    orderService.save(event);
                });
    }
}
