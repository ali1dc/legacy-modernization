package com.legacy.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.legacy.ingestor")
public class IngestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestorApplication.class, args);
	}
}
