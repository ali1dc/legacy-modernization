package com.legacy.shipment.service;

import com.legacy.shipment.model.Address;
import com.legacy.shipment.model.Customer;

public interface CustomerService {

    void save(Customer customer, Address address);
}
