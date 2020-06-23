package com.legacy.ingestor.stream;

import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.events.PaymentEvent;
import com.legacy.ingestor.service.CustomerService;
import com.legacy.ingestor.service.OrderService;
import com.legacy.ingestor.service.PaymentService;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PaymentService paymentService;

    @Bean
    public java.util.function.Consumer<KStream<String, PaymentEvent>> iPayments_1() {

        return input -> input
                .filter((key, event) -> {
                    boolean isDeleted = Objects.equals(event.getOp(), Actions.DELETE);
                    boolean isCreated = (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ));
                    boolean hasLegacyId = event.getAfter().getLegacyId() != null;

                    return  !(isDeleted || (isCreated && hasLegacyId));
                })
                .map((key, event) -> {
                    paymentService.save(event);
                    return KeyValue.pair(key, event);
                });
    }
}
