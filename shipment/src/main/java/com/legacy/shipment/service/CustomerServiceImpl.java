package com.legacy.shipment.service;

import com.legacy.shipment.model.Address;
import com.legacy.shipment.model.Customer;
import com.legacy.shipment.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void save(Customer customer, Address address) {

        customer.setAddress(address);
        customerRepository.save(customer);
        logger.info("customer saved successfully!");
    }
}
