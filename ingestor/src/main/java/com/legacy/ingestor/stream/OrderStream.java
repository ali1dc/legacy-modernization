package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.*;
import com.legacy.ingestor.events.*;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.service.CustomerService;
import com.legacy.ingestor.service.OrderItemService;
import com.legacy.ingestor.service.OrderService;
import com.legacy.ingestor.service.ProductService;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class OrderStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;

    private final Serde<Order> orderSerde;

    public OrderStream() {
        this.orderSerde = new JsonSerde<>(Order.class);
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Order>> iOrder() {
        return input -> input
                .filter((key, value) -> {
                    OrderEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), OrderEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Order order = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        OrderEvent event = jsonMapper.readValue(jsonNode.toString(), OrderEvent.class);
                        LegacyOrder legacyOrder = orderService.save(event);
                        order = event.getAfter();
                        order.setLegacyId(legacyOrder.getOrderId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert order != null;
                    return KeyValue.pair(order.getId(), order);
                })
                .groupByKey(Grouped.with(Serdes.Long(), orderSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.ORDER_STORE))
                .toStream();
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iOrderItem() {

        return cs -> cs.foreach((key, value) -> orderItemService.eventHandler(value));
    }
}
