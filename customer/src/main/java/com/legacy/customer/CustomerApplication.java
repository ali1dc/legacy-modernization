package com.legacy.customer;

import com.legacy.customer.model.Address;
import com.legacy.customer.model.Customer;
import com.legacy.customer.model.CustomerAddress;
import com.legacy.customer.repository.AddressRepository;
import com.legacy.customer.repository.CustomerAddressRepository;
import com.legacy.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class CustomerApplication {

//	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

//	@Autowired
//	private CustomerRepository customerRepository;
//	@Autowired
//	private CustomerAddressRepository customerAddressRepository;
//	@Autowired
//	private AddressRepository addressRepository;

//	@Bean
//	public void testModels() {
//
//		logger.info("saving a customer");
//		Customer customer = Customer.builder()
//				.firstName("Hani")
//				.lastName("Jafari")
//				.email("hani@gmail.com")
//				.phone("202-841-9658")
//				.createdBy("ali")
//				.build();
//		customerRepository.save(customer);
//
//		Address address = Address.builder()
//				.address1("23355 Carters Meadow Ter")
//				.city("Ashburn")
//				.zip("20148")
//				.createdBy("ali")
//				.build();
//		addressRepository.save(address);
//
//		CustomerAddress customerAddress = CustomerAddress.builder()
//				.customer(customer)
//				.address(address)
//				.addressType("billing")
//				.isDefault(true)
//				.build();
//
//		customerAddressRepository.save(customerAddress);
//
//		Optional<Customer> customerOptional = customerRepository.findById(customerAddress.getCustomer().getId());
//
//		if(customerOptional.isPresent())
//			logger.info("Customer record: {}", customerOptional.get());
//		else
//			logger.info("Customer record not found!");
//
//	}
}
