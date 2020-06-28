package com.legacy.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.customer.config.Actions;
import com.legacy.customer.config.AddressTypes;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    private CustomerMapper mapper;

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

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
                insert(event);
                break;
            case Actions.UPDATE:
                update(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public void insert(LegacyCustomerEvent event) {

        // check if message is coming from mod
        if (Objects.equals(event.getAfter().getCreatedBy(), modCreatedBy)) {
            logger.info("The message is coming from mod");
            return;
        }

        Customer customer = mapper.eventToCustomer(event);
        customer.setCreatedBy(legacyCreatedBy);
        customer.setCreatedDate(event.getTimestamp());
        Optional<Customer> existingCustomer = customerRepository.findTopByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            logger.info("Customer: {} exists, do not insert it again!", customer.getEmail());
            return;
        }
        customerRepository.save(customer);

        Address billingAddress = mapper.eventToAddress(event,true);
        billingAddress.setCreatedDate(event.getTimestamp());
        billingAddress.setCreatedBy(legacyCreatedBy);
        billingAddress.setLegacyId(customer.getLegacyId());
        addressRepository.save(billingAddress);

        Address shippingAddress = mapper.eventToAddress(event,false);
        shippingAddress.setCreatedDate(event.getTimestamp());
        shippingAddress.setCreatedBy(legacyCreatedBy);
        shippingAddress.setLegacyId(customer.getLegacyId());
        if(billingAddress.equals(shippingAddress)) {
            shippingAddress = billingAddress;
        }
        addressRepository.save(shippingAddress);

        CustomerAddress customerAddress = CustomerAddress.builder()
                .customer(customer)
                .address(billingAddress)
                .addressType(AddressTypes.BILLING)
                .isDefault(true)
                .build();
        customerAddressRepository.save(customerAddress);
        customerAddress = CustomerAddress.builder()
                .customer(customer)
                .address(shippingAddress)
                .addressType(AddressTypes.SHIPPING)
                .isDefault(false)
                .build();
        customerAddressRepository.save(customerAddress);
    }

    @Override
    public void update(LegacyCustomerEvent event) {

        Optional<Customer> existingCustomer = customerRepository.findTopByEmail(event.getBefore().getEmail());
        if (!existingCustomer.isPresent()) return;

        Customer customer = existingCustomer.get();
        customer.setFirstName(event.getAfter().getFirstName());
        customer.setLastName(event.getAfter().getLastName());
        customer.setEmail(event.getAfter().getEmail());
        customer.setPhone(event.getAfter().getPhone());
        customer.setUpdatedBy(legacyCreatedBy);
        customer.setUpdatedDate(event.getTimestamp());
        customerRepository.save(customer);
        // then handle address update here!
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        optionalCustomer.ifPresent(customer -> {
            if (customer.getLegacyId() == null) {
                customer.setLegacyId(legacyId);
                customer.setUpdatedBy(legacyCreatedBy);
                customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                customerRepository.save(customer);
            }
        });
        Optional<List<CustomerAddress>> customerAddresses = customerAddressRepository.findByCustomerId(id);
        customerAddresses.ifPresent(list -> {
            list.forEach(ca -> {
                Address address = ca.getAddress();
                if (address.getLegacyId() == null) {
                    address.setLegacyId(legacyId);
                    addressRepository.save(address);
                }
            });
        });
    }

    @Override
    public void delete(LegacyCustomerEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
