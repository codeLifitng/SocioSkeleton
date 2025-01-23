package com.SocioSkeleton.notfication_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotficationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotficationServiceApplication.class, args);
	}

}
