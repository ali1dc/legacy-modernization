package com.legacy.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Customer;

public interface CustomerService {
    void eventHandler(String data);
    Customer insert(LegacyCustomerEvent event);
    Customer update(LegacyCustomerEvent event);
    void delete(LegacyCustomerEvent event);
}
