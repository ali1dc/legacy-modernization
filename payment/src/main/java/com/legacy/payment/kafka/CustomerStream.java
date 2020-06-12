package com.legacy.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.dto.*;
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
    public Function<KStream<String, String>, KStream<String, Customer>> pCustomer() {
        return input -> input
                .map((key, value) -> {
                    Customer customer = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        customer = jsonMapper.readValue(jsonNode.toString(), Customer.class);
                        logger.info("Key: {}, Value: {}", key, customer.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, customer);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, Address>> pAddress() {
        return input -> input
                .map((key, value) -> {
                    Address address = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        address = jsonMapper.readValue(jsonNode.toString(), Address.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, address);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, CustomerAddress>> pBillingCustomerAddress() {
        return input -> input
//                .filter((key, value) -> {
//                    CustomerAddress customerAddress = null;
//                    try {
//                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
//                        customerAddress = jsonMapper.readValue(jsonNode.toString(), CustomerAddress.class);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return Objects.equals(customerAddress.getAddressType(), AddressTypes.BILLING);
//                })
                .map((key, value) -> {
                    CustomerAddress customerAddress = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        customerAddress = jsonMapper.readValue(jsonNode.toString(), CustomerAddress.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, customerAddress);
                });
    }

    @Bean
    public Function<KStream<String, CustomerAddress>,
            Function<GlobalKTable<String, Customer>,
                    Function<GlobalKTable<String, Address>,
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
