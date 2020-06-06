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
    private final Serde<LegacyOrder> legacyOrderSerde;
    private final Serde<OrderItem> orderItemSerde;
    private final Serde<EnrichedOrder> enrichedOrderSerde;
    private final Serde<OrderItemOrder> orderItemOrderSerde;
    private final Serde<CustomerOrder> customerOrderSerde;

    public OrderStream() {
        this.orderSerde = new JsonSerde<>(Order.class);
        this.legacyOrderSerde = new JsonSerde<>(LegacyOrder.class);
        this.orderItemSerde = new JsonSerde<>(OrderItem.class);
        this.enrichedOrderSerde = new JsonSerde<>(EnrichedOrder.class);
        this.orderItemOrderSerde = new JsonSerde<>(OrderItemOrder.class);
        this.customerOrderSerde = new JsonSerde<>(CustomerOrder.class);
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Order>> processOrder() {
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
    public java.util.function.Consumer<KStream<String, String>> processOrderItem() {

        return cs -> cs.foreach((key, value) -> orderItemService.eventHandler(value));
    }

//    @Bean
//    public Function<KStream<String, String>, KStream<Long, OrderItem>> processOrderItem() {
//        return input -> input
//                .map((key, value) -> {
//                    OrderItem orderItem = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
//                        OrderItemEvent event = jsonMapper.readValue(jsonNode.toString(), OrderItemEvent.class);
//                        orderItem = event.getAfter();
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return KeyValue.pair(orderItem.getId(), orderItem);
//                });
//    }

//    @Bean
//    public Function<KStream<String, String>, KStream<Long, Customer>> processOrderCustomer() {
//        return input -> input
//                .map((key, value) -> {
//                    Customer customer = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
//                        CustomerEvent event = jsonMapper.readValue(jsonNode.toString(), CustomerEvent.class);
//                        LegacyCustomer legacyCustomer = customerService.save(event);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return KeyValue.pair(customer.getId(), customer);
//                });
//    }

//    @Bean
//    public Function<KStream<String, String>, KStream<Long, Product>> processOrderProduct() {
//        return input -> input
//                .map((key, value) -> {
//                    Product product = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
//                        ProductEvent event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
//                        product = event.getAfter();
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return KeyValue.pair(product.getId(), product);
//                });
//    }

/*
    @Bean
    public Function<KStream<Long, OrderItem>,
            Function<GlobalKTable<Long, Order>,
                    Function<GlobalKTable<Long, Customer>,
                            Function<GlobalKTable<Long, Product>, KStream<Long, EnrichedOrder>>>>> enrichOrder() {

        return orderItems -> (
                orders -> (
                        customers -> (
                                products -> (
                                        orderItems.join(orders,
                                                (orderItemId, orderItem) -> orderItem.getOrderId(),
                                                (orderItem, order) -> new OrderItemOrder(order, orderItem))
                                                //Joined.with(Serdes.Long(), orderItemSerde, orderItemOrderSerde))
                                                .join(customers,
                                                        (id, orderItemOrder) -> orderItemOrder.getOrder().getCustomerId(),
                                                            (orderItemOrder, customer) -> new CustomerOrder(customer, orderItemOrder.getOrder(), orderItemOrder.getOrderItem()))
                                                        .join(products,
                                                            (productId, customerOrder) -> customerOrder.getOrderItem().getProductId(),
                                                            (customerOrder, product) -> {
                                                                return EnrichedOrder.builder()
                                                                        .customer(customerOrder.getCustomer())
                                                                        .order(customerOrder.getOrder())
                                                                        .product(product)
                                                                        .orderItem(customerOrder.getOrderItem())
                                                                        .build();

                                                            })
                                        )
                                )
                        )
                );
    }
*/

//    @Bean
//    public Function<KStream<String, String>, KStream<Long, Order>> processOrder() {
//
//        return input -> input
//                .filter((key, value) -> {
//                    OrderEvent event = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
//                        event = jsonMapper.readValue(jsonNode.toString(), OrderEvent.class);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return !Objects.equals(event.getOp(), Actions.DELETE);
//                })
//                .map((key, value) -> {
//                    Order order = null;
//                    OrderEvent event = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
//                        event = jsonMapper.readValue(jsonNode.toString(), OrderEvent.class);
//                        if (Objects.equals(event.getOp(), Actions.CREATE)) {
//                            order = orderService.save(event);
//                        }
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return KeyValue.pair(event.getAfter().getId(), order);
//                })
//                .groupByKey(Grouped.with(Serdes.Long(), orderSerde))
//                .reduce((value1, value2) -> value2, Materialized.as(StateStores.ORDER_STORE))
//                .toStream();
//    }
}
