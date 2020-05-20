package com.modernized.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.modernized.product")
public class ProductApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProductApplication.class);

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(ProductApplication.class);

		springApplication.run(args);

		logger.info("Product microservice is running!");
	}
}
