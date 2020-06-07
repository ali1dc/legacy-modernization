package com.legacy.customer.service;

import com.legacy.customer.event.LegacyCustomerEvent;
import com.legacy.customer.model.Customer;

public interface CustomerService {
    void eventHandler(String data);
    void insert(LegacyCustomerEvent event);
    void update(LegacyCustomerEvent event);
    void delete(LegacyCustomerEvent event);
}
