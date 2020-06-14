package com.legacy.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.Actions;
import com.legacy.payment.event.OrderEvent;
import com.legacy.payment.repository.OrderRepository;
import com.legacy.payment.service.OrderService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderService orderService;

    @Bean
    public java.util.function.Consumer<KStream<String, String>> pOrders() {

        return input -> input
                .filter((key, value) -> {
                    OrderEvent event = getOrderEvent(value);
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .foreach((key, value) -> {
                    OrderEvent event = getOrderEvent(value);
                    orderService.save(event);
                });
    }

    private OrderEvent getOrderEvent(String data) {
        OrderEvent event = null;
        try {
            event = jsonMapper.readValue(jsonMapper.readTree(data).toString(), OrderEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return event;
    }
}
