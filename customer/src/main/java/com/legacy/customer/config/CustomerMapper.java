package com.legacy.customer.config;

import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Address;
import com.legacy.customer.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Address eventToAddress(LegacyCustomerEvent event, Boolean isBilling) {

        Address address = null;
        if (isBilling) {
            address = Address.builder()
                    .address1(event.getAfter().getBillingAddress1())
                    .address2(event.getAfter().getBillingAddress2())
                    .city(event.getAfter().getBillingCity())
                    .state(event.getAfter().getBillingState())
                    .zip(event.getAfter().getBillingZip())
                    .build();
        } else {
            address = Address.builder()
                    .address1(event.getAfter().getShippingAddress1())
                    .address2(event.getAfter().getShippingAddress2())
                    .city(event.getAfter().getShippingCity())
                    .state(event.getAfter().getShippingState())
                    .zip(event.getAfter().getShippingZip())
                    .build();
        }
        return address;
    }

    public Customer eventToCustomer(LegacyCustomerEvent event) {

        return Customer.builder()
                .firstName(event.getAfter().getFirstName())
                .lastName(event.getAfter().getLastName())
                .phone(event.getAfter().getPhone())
                .email(event.getAfter().getEmail())
                .legacyId(event.getAfter().getCustomerId())
                .build();
    }
}
