package com.legacy.payment.service;

import com.legacy.payment.dto.Address;
import com.legacy.payment.dto.Customer;

public interface CustomerService {

    void save(Customer customer, Address address);
}
