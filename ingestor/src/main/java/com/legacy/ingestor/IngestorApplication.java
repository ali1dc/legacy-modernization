package com.legacy.ingestor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.legacy.ingestor")
public class IngestorApplication {

	private static Logger logger = LoggerFactory.getLogger(IngestorApplication.class);

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(IngestorApplication.class);

		springApplication.run(args);

		logger.info("Legacy Ingestor microservice is running!");
	}
}
