package com.legacy.customer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.customer.dto.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;

    @Bean
    public Function<KStream<String, String>, KStream<String, CustomerDto>> pCustomer() {
        return input -> input
                .map((key, value) -> {
                    CustomerDto customer = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        customer = jsonMapper.readValue(jsonNode.toString(), CustomerDto.class);
                        logger.info("Key: {}, Value: {}", key, customer.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, customer);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, AddressDto>> pAddress() {
        return input -> input
                .map((key, value) -> {
                    AddressDto address = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        address = jsonMapper.readValue(jsonNode.toString(), AddressDto.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, address);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, CustomerAddressDto>> pBillingCustomerAddress() {
        return input -> input
                .map((key, value) -> {
                    CustomerAddressDto customerAddress = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        customerAddress = jsonMapper.readValue(jsonNode.toString(), CustomerAddressDto.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, customerAddress);
                });
    }

    @Bean
    public Function<KStream<String, CustomerAddressDto>,
            Function<GlobalKTable<String, CustomerDto>,
                    Function<GlobalKTable<String, AddressDto>,
                            KStream<String, EnrichedCustomer>>>> enrichedCustomer() {
        return customerAddresses -> (
                customers -> (
                        addresses -> (
                                customerAddresses.leftJoin(customers,
                                        (customerAddressId, customerAddress) -> customerAddress.getCustomerId().toString(),
                                        CustomerAddressCustomer::new)
                                        .leftJoin(addresses,
                                                (id, customerAddressCustomer) -> customerAddressCustomer.getCustomerAddress().getAddressId().toString(),
                                                (customerAddressCustomer, address) -> EnrichedCustomer.builder()
                                                        .customer(customerAddressCustomer.getCustomer())
                                                        .customerAddress(customerAddressCustomer.getCustomerAddress())
                                                        .address(address)
                                                        .build())
                        )
                )
        );
    }
}
