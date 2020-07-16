package com.legacy.ingestor.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.service.CustomerService;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CustomerStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CustomerService customerService;

    @Bean
    public java.util.function.Consumer<KStream<String, CustomerEvent>> iCustomers() {

        return events -> events
                .foreach((key, event) -> {
                    customerService.handler(event);
                });
    }
}
