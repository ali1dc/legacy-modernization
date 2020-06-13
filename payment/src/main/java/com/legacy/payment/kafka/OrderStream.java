package com.legacy.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.StateStores;
import com.legacy.payment.dto.*;
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
    public Function<KStream<String, String>, KStream<Long, Float>> pOrderTotal() {
        return input -> input
                .map((key, value) -> {
                    OrderItem item = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        item = jsonMapper.readValue(jsonNode.toString(), OrderItem.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert item != null;
                    Float lineTotal = item.getQuantity() * item.getUnitPrice();
                    return KeyValue.pair(item.getOrderId(), lineTotal);
                })
                .groupByKey(Grouped.with(Serdes.Long(), Serdes.Float()))
                .reduce(Float::sum, Materialized.as(StateStores.ORDER_TOTAL))
                .toStream();
    }
}
