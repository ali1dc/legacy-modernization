package com.legacy.payment.service;

import com.legacy.payment.dto.Address;
import com.legacy.payment.dto.Customer;
import com.legacy.payment.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired(required = false )
    private ModelMapper mapper;

    @Override
    public void save(Customer customer, Address address) {

        com.legacy.payment.model.Customer customerModel = mapper.map(customer, com.legacy.payment.model.Customer.class);
        com.legacy.payment.model.Address addressModel = mapper.map(address, com.legacy.payment.model.Address.class);

        customerModel.setAddress(addressModel);

        customerRepository.save(customerModel);
    }
}
