package com.legacy.customer.kafka;

import com.legacy.customer.service.CustomerService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CustomerConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerService customerService;

    @Bean
    public java.util.function.Consumer<KStream<Object, String>> processCustomer() {

        return input -> input
                .foreach((key, value) -> {
                    logger.info("start processing legacy messages");
                    customerService.eventHandler(value);
                });
    }
}
