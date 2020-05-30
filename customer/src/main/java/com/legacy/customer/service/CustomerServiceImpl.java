package com.legacy.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.customer.config.Actions;
import com.legacy.customer.config.CustomerMapper;
import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Address;
import com.legacy.customer.model.Customer;
import com.legacy.customer.model.CustomerAddress;
import com.legacy.customer.repository.AddressRepository;
import com.legacy.customer.repository.CustomerAddressRepository;
import com.legacy.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(String data){

        JsonNode jsonNode = null;
        LegacyCustomerEvent event = null;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), LegacyCustomerEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        if (Objects.equals(event.getOp(), Actions.CREATE)) {
            insert(event);
        } else if (Objects.equals(event.getOp(), Actions.UPDATE)) {
            update(event);
        } else if (Objects.equals(event.getOp(), Actions.DELETE)) {
            delete(event);
        }
    }

    @Autowired
    private CustomerMapper mapper;

    @Override
    public Customer insert(LegacyCustomerEvent event) {

        Customer customer = mapper.eventToCustomer(event);
        customer.setCreatedBy(legacyCreatedBy);
        customer.setCreatedDate(event.getTimestamp());
        customerRepository.save(customer);

        Address billingAddress = mapper.eventToAddress(event,true);
        billingAddress.setCreatedDate(event.getTimestamp());
        billingAddress.setCreatedBy(legacyCreatedBy);
        addressRepository.save(billingAddress);

        Address shippingAddress = mapper.eventToAddress(event,false);
        shippingAddress.setCreatedDate(event.getTimestamp());
        shippingAddress.setCreatedBy(legacyCreatedBy);
        if(billingAddress.equals(shippingAddress)) {
            shippingAddress = billingAddress;
        }
        addressRepository.save(shippingAddress);

        CustomerAddress customerAddress = CustomerAddress.builder()
                .customer(customer)
                .address(billingAddress)
                .addressType("billing")
                .isDefault(true)
                .build();
        customerAddressRepository.save(customerAddress);
        customerAddress = CustomerAddress.builder()
                .customer(customer)
                .address(shippingAddress)
                .addressType("shipping")
                .isDefault(false)
                .build();
        customerAddressRepository.save(customerAddress);

        return customer;
    }

    @Override
    public Customer update(LegacyCustomerEvent event) {

        Optional<Customer> existingCustomer = customerRepository.findByLegacyId(event.getBefore().getCustomerId());
        if (!existingCustomer.isPresent()) {
            return insert(event);
        }

        Customer customer = existingCustomer.get();
        customer.setFirstName(event.getAfter().getFirstName());
        customer.setLastName(event.getAfter().getLastName());
        customer.setEmail(event.getAfter().getEmail());
        customer.setPhone(event.getAfter().getPhone());
        customer.setUpdatedBy(legacyCreatedBy);
        customer.setUpdatedDate(event.getTimestamp());
        customerRepository.save(customer);
        // then handle address update here!

        return customer;
    }

    @Override
    public void delete(LegacyCustomerEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
