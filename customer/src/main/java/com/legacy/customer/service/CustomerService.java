package com.legacy.customer.service;

import com.legacy.customer.dto.EnrichedCustomer;
import com.legacy.customer.dto.EventAcknowledge;
import com.legacy.customer.event.LegacyCustomerEvent;

public interface CustomerService {
    void eventHandler(LegacyCustomerEvent event);
    void insert(LegacyCustomerEvent event);
    void insert(EnrichedCustomer event);
    void update(LegacyCustomerEvent event);
    void update(EventAcknowledge acknowledge);
    void delete(LegacyCustomerEvent event);
}
