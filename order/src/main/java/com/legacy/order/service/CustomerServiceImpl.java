package com.legacy.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.dto.Outbox;
import com.legacy.order.event.CustomerEvent;
import com.legacy.order.model.Customer;
import com.legacy.order.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired(required = false)
    private ModelMapper mapper;
    @Autowired
    private CustomerRepository customerRepository;

    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(CustomerEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
                save(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public void save(CustomerEvent event) {

        Outbox outbox = event.getAfter();

        try {
            JsonNode node = jsonMapper.readTree(outbox.getPayload().asText());
            Customer customer = jsonMapper.convertValue(node.at("/customer"), Customer.class);

            customerRepository.save(customer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(CustomerEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
