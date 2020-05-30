package com.legacy.customer.controller;

import com.legacy.customer.model.Address;
import com.legacy.customer.model.Customer;
import com.legacy.customer.model.CustomerAddress;
import com.legacy.customer.repository.AddressRepository;
import com.legacy.customer.repository.CustomerAddressRepository;
import com.legacy.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    @Autowired
    private AddressRepository addressRepository;

    @GetMapping
    public List<Customer>  customers() {
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach( customer -> customers.add(customer));
        return customers;
    }

    @GetMapping("/{id}")
    public Customer customer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).get();
        List<CustomerAddress> customerAddresses = customerAddressRepository.findByCustomerId(customer.getId()).get();
        customer.setAddresses(new HashSet<>());
        Set<Address> addresses = new HashSet<>();
        customerAddresses.forEach(customerAddress -> {
            addresses.add(customerAddress.getAddress());
        });
        customer.setAddresses(addresses);
        return customer;
    }
}
