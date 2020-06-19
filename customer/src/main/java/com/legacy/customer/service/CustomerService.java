package com.legacy.customer.service;

import com.legacy.customer.event.LegacyCustomerEvent;

public interface CustomerService {
    void eventHandler(String data);
    void insert(LegacyCustomerEvent event);
    void update(LegacyCustomerEvent event);
    void update(Long id, Long legacyId);
    void delete(LegacyCustomerEvent event);
}
