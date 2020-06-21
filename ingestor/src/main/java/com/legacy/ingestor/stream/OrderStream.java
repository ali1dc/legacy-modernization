package com.legacy.ingestor.stream;

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
    private final Serde<Customer> customerSerde;
    public OrderStream() {
        this.orderSerde = new JsonSerde<>(Order.class);
        this.customerSerde = new JsonSerde<>(Customer.class);
    }

//    @Bean
//    public BiConsumer<KStream<String, OrderEvent>, GlobalKTable<String, CustomerEvent>> iOrderCustomer() {
//
//        return (orderStream, customerTable) -> {
//            orderStream.leftJoin(customerTable, (left, right) -> right.getAfter().getCustomerId().toString(),
//                    (orderEvent, customerEvent) -> {
//                        OrderCustomer oc = OrderCustomer.builder().id(orderEvent.getAfter().getId()).order(orderEvent.getAfter()).customer(customerEvent.getAfter()).build();
//                        return oc;
//                    })
//                    .foreach((key, value) -> {
//                        logger.info("key: {}, order id: {}, customer email: {}", key, value.getOrder().getId(), value.getCustomer().getEmail());
//
//                    });
//        };
//    }

    @Bean
    public java.util.function.Consumer<KStream<String, OrderEvent>> iOrders() {

        return input -> input
                .filter((key, event) -> {
                    assert event != null;
                    boolean isDeleted = Objects.equals(event.getOp(), Actions.DELETE);
                    boolean isCreated = (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ));
                    boolean hasLegacyId = event.getAfter().getLegacyId() != null;

                    return !(isDeleted || (isCreated && hasLegacyId));
                })
                .map((key, event) -> {
                    LegacyOrder legacyOrder = orderService.save(event);
                    Order order = event.getAfter();
                    order.setLegacyId(legacyOrder.getOrderId());
                    return KeyValue.pair(order.getId(), order);
                })
                .groupByKey(Grouped.with(Serdes.Long(), orderSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.ORDER_STORE));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iOrderItems() {

        return cs -> cs.foreach((key, value) -> {
            orderItemService.eventHandler(value);
        });
    }
}
