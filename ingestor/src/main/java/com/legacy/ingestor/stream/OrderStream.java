package com.legacy.ingestor.stream;

import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.events.*;
import com.legacy.ingestor.service.OrderItemService;
import com.legacy.ingestor.service.OrderService;
import org.apache.kafka.streams.kstream.*;
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
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

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
                .foreach((key, event) -> {
                    switch (event.getOp()) {
                        case Actions.CREATE:
                        case Actions.READ:
                            orderService.insert(event);
                            break;
                        case Actions.UPDATE:
                            orderService.update(event);
                            break;
                        case Actions.DELETE:
                            orderService.delete(event);
                            break;
                    }
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, OrderItemEvent>> iOrderItems() {

        return cs -> cs.foreach((key, event) -> {
            orderItemService.eventHandler(event);
        });
    }
}
