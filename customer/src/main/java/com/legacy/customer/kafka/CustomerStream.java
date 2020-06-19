package com.legacy.customer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.customer.dto.*;
import com.legacy.customer.event.AddressEvent;
import com.legacy.customer.event.CustomerAddressEvent;
import com.legacy.customer.event.CustomerEvent;
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
    public Function<KStream<String, CustomerEvent>, KStream<String, CustomerDto>> pCustomer() {
        return input -> input
                .map((key, event) -> KeyValue.pair(key, event.getAfter()));
    }

    @Bean
    public Function<KStream<String, AddressEvent>, KStream<String, AddressDto>> pAddress() {
        return input -> input
                .map((key, event) -> KeyValue.pair(key, event.getAfter()));
    }

    @Bean
    public Function<KStream<String, CustomerAddressEvent>, KStream<String, CustomerAddressDto>> pCustomerAddress() {
        return input -> input
                .map((key, event) -> KeyValue.pair(key, event.getAfter()));
    }

/**    @Bean
    public Function<KStream<String, CustomerAddressEvent>,
            Function<GlobalKTable<String, CustomerEvent>,
                    Function<GlobalKTable<String, AddressEvent>,
                            KStream<String, EnrichedCustomer>>>> coolCustomers() {
        return customerAddresses -> (
                customers -> (
                        addresses -> (
                                customerAddresses.leftJoin(customers,
                                        (customerAddressId, customerAddress) -> customerAddress.getAfter().getCustomerId().toString(),
                                        CustomerAddressCustomer::new)
                                        .leftJoin(addresses,
                                                (id, customerAddressCustomer) -> customerAddressCustomer.getCustomerAddressEvent().getAfter().getAddressId().toString(),
                                                (customerAddressCustomer, address) -> EnrichedCustomer.builder()
                                                        .customer(customerAddressCustomer.getCustomerEvent().getAfter())
                                                        .customerAddress(customerAddressCustomer.getCustomerAddressEvent().getAfter())
                                                        .address(address.getAfter())
                                                        .build())
                        )
                )
        );
    }
**/

    @Bean
    public Function<KStream<String, CustomerAddressDto>,
            Function<GlobalKTable<String, CustomerDto>,
                    Function<GlobalKTable<String, AddressDto>,
                            KStream<String, EnrichedCustomer>>>> enrichedCustomers() {
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
