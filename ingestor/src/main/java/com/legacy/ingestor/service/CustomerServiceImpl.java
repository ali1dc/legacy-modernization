package com.legacy.ingestor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.AddressTypes;
import com.legacy.ingestor.config.KafkaTopics;
import com.legacy.ingestor.dto.EnrichedCustomer;
import com.legacy.ingestor.dto.EventAcknowledge;
import com.legacy.ingestor.dto.OrderStatus;
import com.legacy.ingestor.dto.Outbox;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.repository.CustomerRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper jsonMapper;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;
    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;

    @Override
    public void handler(CustomerEvent event) {

        switch (event.getAfter().getAction()) {
            case Actions.CREATE:
                insert(event.getAfter());
                break;
        }
    }

    @Override
    public void insert(Outbox outbox) {

        if (Objects.equals(outbox.getCreateBy(), legacyCreatedBy)) {
            logger.info("This is a legacy event, no action needed!");
            return;
        }
        JsonNode node = null;
        try {
            node = jsonMapper.readTree(outbox.getPayload().asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        EnrichedCustomer customer = jsonMapper.convertValue(node, EnrichedCustomer.class);
        if (customerRepository.findTopByEmail(customer.getCustomer().getEmail()).isPresent()) {
            logger.info("Customer exists, no action needed!");
            return;
        }
        LegacyCustomer legacyCustomer = LegacyCustomer.builder()
            .firstName(customer.getCustomer().getFirstName())
            .lastName(customer.getCustomer().getLastName())
            .phone(customer.getCustomer().getPhone())
            .email(customer.getCustomer().getEmail())
            .createdBy(modCreatedBy)
            .build();

        customer.getAddresses().forEach(address -> {
            if (Objects.equals(address.getAddressType(), AddressTypes.BILLING )) {
                legacyCustomer.setBillingAddress1(address.getAddress1());
                legacyCustomer.setBillingAddress2(address.getAddress2());
                legacyCustomer.setBillingCity(address.getCity());
                legacyCustomer.setBillingState(address.getState());
                legacyCustomer.setBillingZip(address.getZip());
            } else {
                legacyCustomer.setShippingAddress1(address.getAddress1());
                legacyCustomer.setShippingAddress2(address.getAddress2());
                legacyCustomer.setShippingCity(address.getCity());
                legacyCustomer.setShippingState(address.getState());
                legacyCustomer.setShippingZip(address.getZip());
            }
        });
        customerRepository.save(legacyCustomer);

        sendAcknowledge(EventAcknowledge.builder()
                .EventId(outbox.getId())
                .id(customer.getCustomer().getId())
                .legacyId(legacyCustomer.getId())
                .build());
    }

    private void sendAcknowledge(EventAcknowledge ack) {

        try {
            kafkaTemplate.send(KafkaTopics.CUSTOMER,
                    ack.getEventId().toString(),
                    jsonMapper.writeValueAsString(ack));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }
}
