package com.legacy.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.CustomerEvent;
import com.legacy.order.model.Customer;
import com.legacy.order.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void eventHandler(String data) {

        JsonNode jsonNode;
        CustomerEvent event;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), CustomerEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.UPDATE:
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

        Optional<Customer> optionalCustomer = customerRepository.findById(event.getAfter().getId());
        Customer customer;
        if (optionalCustomer.isPresent()) {
            customer = optionalCustomer.get();
            customer.setFirstName(event.getAfter().getFirstName());
            customer.setLastName(event.getAfter().getLastName());
            customer.setLegacyId(event.getAfter().getLegacyId());
            customer.setPhone(event.getAfter().getPhone());
            customer.setEmail(event.getAfter().getEmail());
            customer.setUpdatedBy("modernized");
            customer.setUpdatedDate(new Date());
        } else {
            customer = event.getAfter();
        }

        customerRepository.save(customer);
    }

    @Override
    public void delete(CustomerEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
