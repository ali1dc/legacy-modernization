package com.legacy.ingestor.service;

import com.legacy.ingestor.events.CustomerAddressEvent;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.model.LegacyCustomer;

public interface CustomerService {

    void legacyHandler(CustomerAddressEvent event);
    LegacyCustomer save(CustomerAddressEvent event);
    void update(CustomerEvent event);
    void delete(CustomerAddressEvent event);
}
