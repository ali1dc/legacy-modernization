package com.legacy.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.Actions;
import com.legacy.payment.event.PaymentEvent;
import com.legacy.payment.service.PaymentService;
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
    private ObjectMapper jsonMapper;
    @Autowired
    private PaymentService paymentService;

    @Bean
    public java.util.function.Consumer<KStream<String, String>> payments() {

        return input -> input
                .filter((key, value) -> {
                    PaymentEvent event = getPaymentEvent(value);
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .foreach((key, value) -> {
                    PaymentEvent event = getPaymentEvent(value);
                    paymentService.eventHandler(event);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacyPaymentIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    paymentService.update(id, legacyId);
                });
    }

    private PaymentEvent getPaymentEvent(String data) {

        JsonNode jsonNode;
        PaymentEvent event = null;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            Integer isSuccessful = jsonNode.at("/after/successfull").asInt();
            event = jsonMapper.readValue(jsonNode.toString(), PaymentEvent.class);
            event.getAfter().setSuccessful(isSuccessful == 1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return event;
    }
}
