package com.legacy.ingestor.service;

import com.legacy.ingestor.config.AddressTypes;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Address;
import com.legacy.ingestor.dto.Customer;
import com.legacy.ingestor.events.CustomerAddressEvent;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.repository.CustomerRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public LegacyCustomer save(CustomerAddressEvent event) {

        logger.info("inserting the customer record");
        ReadOnlyKeyValueStore<Long, Customer> customerStore =
                interactiveQueryService.getQueryableStore(StateStores.CUSTOMER_STORE, QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<Long, Address> addressStore =
                interactiveQueryService.getQueryableStore(StateStores.ADDRESS_STORE, QueryableStoreTypes.keyValueStore());

        Customer customer = customerStore.get(event.getAfter().getCustomerId());
        Address address = addressStore.get(event.getAfter().getAddressId());
        // insert customer if not exists in legacy db
        Optional<LegacyCustomer> customerOptional = customerRepository.findTopByEmail(customer.getEmail());
        LegacyCustomer legacyCustomer;
        if (customerOptional.isPresent()) {
            logger.info("customer exists in legacy, we just update the address!");
            legacyCustomer = customerOptional.get();
        }
        else {
            legacyCustomer = LegacyCustomer.builder()
                    .id(null)
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .phone(customer.getPhone())
                    .email(customer.getEmail())
                    .createdBy(modCreatedBy)
                    .billingAddress1("")
                    .billingCity("")
                    .billingState("")
                    .billingZip("")
                    .shippingAddress1("")
                    .shippingAddress2("")
                    .shippingCity("")
                    .shippingState("")
                    .shippingZip("")
                    .build();
        }
        if (Objects.equals(event.getAfter().getAddressType(), AddressTypes.BILLING)) {
            legacyCustomer.setBillingAddress1(address.getAddress1());
            legacyCustomer.setBillingAddress2(address.getAddress2());
            legacyCustomer.setBillingCity(address.getCity());
            legacyCustomer.setBillingState(address.getState());
            legacyCustomer.setBillingZip(address.getZip());
        } else if (Objects.equals(event.getAfter().getAddressType(), AddressTypes.SHIPPING)) {
            legacyCustomer.setShippingAddress1(address.getAddress1());
            legacyCustomer.setShippingAddress2(address.getAddress2());
            legacyCustomer.setShippingCity(address.getCity());
            legacyCustomer.setShippingState(address.getState());
            legacyCustomer.setShippingZip(address.getZip());
        }

        customerRepository.save(legacyCustomer);

        return legacyCustomer;
    }

    @Override
    public LegacyCustomer save(CustomerEvent event) {

        Optional<LegacyCustomer> customerOptional = customerRepository.findTopByEmail(event.getAfter().getEmail());
        LegacyCustomer customer;
        if (customerOptional.isPresent()) {
            logger.info("customer update action");
            customer = customerOptional.get();
            customer.setFirstName(event.getAfter().getFirstName());
            customer.setLastName(event.getAfter().getLastName());
            customer.setPhone(event.getAfter().getPhone());
            customer.setEmail(event.getAfter().getEmail());
        } else {
            logger.info("customer insert action");
            customer = LegacyCustomer.builder()
                    .id(null)
                    .firstName(event.getAfter().getFirstName())
                    .lastName(event.getAfter().getLastName())
                    .phone(event.getAfter().getPhone())
                    .email(event.getAfter().getEmail())
                    .createdBy(modCreatedBy)
                    .billingAddress1("")
                    .billingCity("")
                    .billingState("")
                    .billingZip("")
                    .shippingAddress1("")
                    .shippingAddress2("")
                    .shippingCity("")
                    .shippingState("")
                    .shippingZip("")
                    .build();
        }

        customerRepository.save(customer);
        return customer;
    }

    @Override
    public void delete(CustomerAddressEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
