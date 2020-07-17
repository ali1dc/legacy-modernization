package com.legacy.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.legacy.customer.config.Actions;
import com.legacy.customer.config.CustomerMapper;
import com.legacy.customer.dto.*;
import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Address;
import com.legacy.customer.model.Customer;
import com.legacy.customer.model.CustomerAddress;
import com.legacy.customer.model.OutboxEvent;
import com.legacy.customer.repository.AddressRepository;
import com.legacy.customer.repository.CustomerAddressRepository;
import com.legacy.customer.repository.CustomerRepository;
import com.legacy.customer.repository.OutboxEventRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private OutboxEventRepository outboxEventRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired(required = false )
    private ModelMapper mapper;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(LegacyCustomerEvent event){

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
        LegacyCustomer legacyCustomer = event.getAfter();
        if (Objects.equals(legacyCustomer.getCreatedBy(), modCreatedBy)) {
            logger.info("The message is coming from mod");
            return;
        }
        Optional<Customer> existingCustomer = customerRepository.findTopByEmail(legacyCustomer.getEmail());
        if (existingCustomer.isPresent()) {
            logger.info("Customer: {} exists, do not insert it again!", event.getAfter().getEmail());
            return;
        }
        CustomerDto customerDto = mapper.map(legacyCustomer, CustomerDto.class);
        customerDto.setCreatedBy(legacyCreatedBy);
        customerDto.setCreatedDate(event.getTimestamp());

        List<AddressDto> addresses = customerMapper.eventToAddress(event.getAfter());
        addresses.forEach(add -> {
            add.setCreatedDate(event.getTimestamp());
            add.setCreatedBy(legacyCreatedBy);
            add.setLegacyId(customerDto.getLegacyId());
        });
        insert(EnrichedCustomer.builder()
                .customer(customerDto)
                .addresses(addresses)
                .build());
    }

    @Override
    @Transactional
    public void insert(EnrichedCustomer event) {

        Customer customer = mapper.map(event.getCustomer(), Customer.class);
        customerRepository.save(customer);
        List<AddressDto> addresses = event.getAddresses();
        addresses.forEach(addressDto -> {
            Address address = mapper.map(addressDto, Address.class);
            addressRepository.save(address);
            customerAddressRepository.save(CustomerAddress.builder()
                    .customer(customer)
                    .address(address)
                    .addressType(addressDto.getAddressType())
                    .isDefault(addressDto.getIsDefault())
                    .build());
            addressDto.setId(address.getId());
        });
        // create outbox event
        CustomerDto customerDto = mapper.map(customer, CustomerDto.class);
        OutboxEvent outboxEvent = getOutboxEvent(customerDto, addresses);
        outboxEventRepository.save(outboxEvent);
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
    public void update(EventAcknowledge acknowledge) {

        Optional<Customer> optionalCustomer = customerRepository.findById(acknowledge.getId());
        optionalCustomer.ifPresent(customer -> {
            if (customer.getLegacyId() == null) {
                customer.setLegacyId(acknowledge.getLegacyId());
                customer.setUpdatedBy(legacyCreatedBy);
                customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                customerRepository.save(customer);
            }
        });
        Optional<List<CustomerAddress>> customerAddresses = customerAddressRepository.findByCustomerId(acknowledge.getId());
        customerAddresses.ifPresent(list -> {
            list.forEach(ca -> {
                Address address = ca.getAddress();
                if (address.getLegacyId() == null) {
                    address.setLegacyId(acknowledge.getLegacyId());
                    addressRepository.save(address);
                }
            });
        });
        outboxEventRepository.findById(acknowledge.getEventId()).ifPresent(event -> {
            event.setProcessed(true);
            outboxEventRepository.save(event);
        });
    }

    @Override
    public void delete(LegacyCustomerEvent event) {

        logger.info("deleting records; not implemented yet!");
    }

    private OutboxEvent getOutboxEvent(CustomerDto customer, List<AddressDto> addresses) {

        ObjectNode payload = jsonMapper.createObjectNode();
        payload.put("id", customer.getId());
        payload.putPOJO("customer", customer);

        ArrayNode addressNodes = payload.putArray("addresses");
        addresses.forEach(add -> addressNodes.add(jsonMapper.valueToTree(add)));

        return OutboxEvent.builder()
                .type("CustomerEvent")
                .action("c")
                .processed(false)
                .payload(payload)
                .createBy(customer.getCreatedBy())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
