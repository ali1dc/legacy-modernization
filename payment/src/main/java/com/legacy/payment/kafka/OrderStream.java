package com.legacy.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.StateStores;
import com.legacy.payment.dto.*;
import com.legacy.payment.event.OrderItemEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;

    @Bean
    public Function<KStream<String, OrderItemEvent>, KStream<Long, Float>> pOrderTotal() {
        return input -> input
                .map((key, event) -> {
                    OrderItem item = event.getAfter();
                    Float lineTotal = item.getQuantity() * item.getUnitPrice();
                    return KeyValue.pair(item.getOrderId(), lineTotal);
                })
                .groupByKey(Grouped.with(Serdes.Long(), Serdes.Float()))
                .reduce(Float::sum, Materialized.as(StateStores.ORDER_TOTAL))
                .toStream();
    }
}
