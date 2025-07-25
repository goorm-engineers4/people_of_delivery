package com.example.cloudfour.peopleofdelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PeopleofdeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleofdeliveryApplication.class, args);
	}

}
