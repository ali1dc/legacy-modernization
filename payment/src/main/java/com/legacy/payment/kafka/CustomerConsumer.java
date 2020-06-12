package com.legacy.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.AddressTypes;
import com.legacy.payment.dto.Customer;
import com.legacy.payment.dto.CustomerAddress;
import com.legacy.payment.dto.EnrichedCustomer;
import com.legacy.payment.service.CustomerService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CustomerService customerService;

    @Bean
    public java.util.function.Consumer<KStream<String, String>> pCustomers() {

        return input -> input
                .filter((key, value) -> {
                    CustomerAddress customerAddress = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/customerAddress");
                        customerAddress = jsonMapper.readValue(jsonNode.toString(), CustomerAddress.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return Objects.equals(customerAddress.getAddressType(), AddressTypes.BILLING);
                })
                .foreach((key, value) -> {
                    EnrichedCustomer customer = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value);
                        customer = jsonMapper.readValue(jsonNode.toString(), EnrichedCustomer.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    logger.info("consumed --> key: {}, customer id: {}, address id: {}, address type: {}",
                            key,
                            customer.getCustomer().getId(),
                            customer.getAddress().getId(),
                            customer.getCustomerAddress().getAddressType());

                    customerService.save(customer.getCustomer(), customer.getAddress());
                });
    }
}
