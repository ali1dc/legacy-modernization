package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.*;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.service.CustomerService;
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

@Component
public class CustomerStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CustomerService customerService;

    private final Serde<Customer> customerSerde;
    private final Serde<Address> addressSerde;


    public CustomerStream() {
        this.customerSerde = new JsonSerde<>(Customer.class);
        this.addressSerde = new JsonSerde<>(Address.class);
    }

    /**
    @Bean
    public Function<KStream<String, CustomerEvent>, KStream<Long, Customer>> iCustomer() {

        return input -> input
                .filter((key, event) -> {
                    boolean isDeleted = Objects.equals(event.getOp(), Actions.DELETE);
                    boolean isCreated = (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ));
                    boolean hasLegacyId = event.getAfter().getLegacyId() != null;

                    return !(isDeleted || (isCreated && hasLegacyId));
                })
                .map((key, event) -> {
                    Customer customer = event.getAfter();
                    LegacyCustomer legacyCustomer = customerService.save(event);
                    customer.setLegacyId(legacyCustomer.getId());

                    return KeyValue.pair(customer.getId(), customer);
                })
                .groupByKey(Grouped.with(Serdes.Long(), customerSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.CUSTOMER_STORE))
                .toStream();
    }

    @Bean
    public Function<KStream<String, AddressEvent>, KStream<Long, Address>> iAddress() {

        return input -> input
                .filter((key, event) -> !Objects.equals(event.getOp(), Actions.DELETE))
                .map((key, event) -> KeyValue.pair(event.getAfter().getId(), event.getAfter()))
                .groupByKey(Grouped.with(Serdes.Long(), addressSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.ADDRESS_STORE))
                .toStream();
    }
     **/

    @Bean
    public java.util.function.Consumer<KStream<String, CustomerEvent>> iCustomerStateStore() {

            return input -> input
                    .map((key, event) -> {
                        Customer customer = event.getAfter();
                        return KeyValue.pair(customer.getId(), customer);
                    })
                    .groupByKey(Grouped.with(Serdes.Long(), customerSerde))
                    .reduce((value1, value2) -> value2, Materialized.as(StateStores.CUSTOMER_STORE));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iCustomerInsert() {

        return customers -> customers
                .foreach((key, value) -> {
                    EnrichedCustomer customer;
                    try {
                        customer = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), EnrichedCustomer.class);

                        customerService.insert(customer);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, CustomerEvent>> iCustomerUpdate() {

        return customers -> customers
                .filter((key, event) -> Objects.equals(event.getOp(), Actions.UPDATE))
                .foreach((key, event) -> {
                    customerService.update(event);
                });
    }
}
