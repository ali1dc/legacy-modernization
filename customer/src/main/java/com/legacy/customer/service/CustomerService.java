package com.legacy.customer.service;

import com.legacy.customer.dto.EnrichedCustomer;
import com.legacy.customer.dto.EventAcknowledge;
import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Customer;

import java.util.Optional;

public interface CustomerService {
    void eventHandler(LegacyCustomerEvent event);
    void insert(LegacyCustomerEvent event);
    void insert(EnrichedCustomer enrichedCustomer, String creator);
    void update(EnrichedCustomer enrichedCustomer, String creator);
    void update(LegacyCustomerEvent event);
    void update(EventAcknowledge acknowledge);
    void delete(LegacyCustomerEvent event);
    Optional<Customer> findByEmail(String email);
}
