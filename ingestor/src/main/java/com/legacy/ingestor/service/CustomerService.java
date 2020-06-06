package com.legacy.ingestor.service;

import com.legacy.ingestor.events.CustomerAddressEvent;
import com.legacy.ingestor.events.CustomerEvent;
import com.legacy.ingestor.model.LegacyCustomer;

public interface CustomerService {

    LegacyCustomer save(CustomerAddressEvent event);
    LegacyCustomer save(CustomerEvent event);
    void delete(CustomerAddressEvent event);
}
