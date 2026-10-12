package com.example.image_processing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ImageProcessingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageProcessingServiceApplication.class, args);
	}

}
