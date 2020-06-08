package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Address;
import com.legacy.ingestor.dto.Customer;
import com.legacy.ingestor.events.AddressEvent;
import com.legacy.ingestor.events.CustomerAddressEvent;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.service.CustomerService;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

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

    @Bean
    public Function<KStream<String, String>, KStream<Long, Customer>> iCustomer() {

        return input -> input
                .filter((key, value) -> {
                    CustomerEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), CustomerEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    boolean isDeleted = Objects.equals(event.getOp(), Actions.DELETE);
                    boolean isCreated = (Objects.equals(event.getOp(), Actions.CREATE) || Objects.equals(event.getOp(), Actions.READ));
                    boolean hasLegacyId = event.getAfter().getLegacyId() != null;

                    return !(isDeleted || (isCreated && hasLegacyId));
                })
                .map((key, value) -> {
                    Customer customer = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        CustomerEvent event = jsonMapper.readValue(jsonNode.toString(), CustomerEvent.class);
                        customer = event.getAfter();
                        LegacyCustomer legacyCustomer = customerService.save(event);
                        customer.setLegacyId(legacyCustomer.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(customer.getId(), customer);
                })
                .groupByKey(Grouped.with(Serdes.Long(), customerSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.CUSTOMER_STORE))
                .toStream();
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Address>> iAddress() {

        return input -> input
                .filter((key, value) -> {
                    AddressEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), AddressEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Address address = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        AddressEvent event = jsonMapper.readValue(jsonNode.toString(), AddressEvent.class);
                        address = event.getAfter();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert address != null;
                    return KeyValue.pair(address.getId(), address);
                })
                .groupByKey(Grouped.with(Serdes.Long(), addressSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.ADDRESS_STORE))
                .toStream();
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iCustomerAddress() {

        return ca -> ca
                .foreach((key, value) -> {
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        CustomerAddressEvent event = jsonMapper.readValue(jsonNode.toString(), CustomerAddressEvent.class);
                        if (!Objects.equals(event.getOp(), Actions.DELETE)) {
                            customerService.save(event);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
    }
}
