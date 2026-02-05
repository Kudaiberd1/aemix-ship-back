package com.example.aemix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AemixApplication {

	public static void main(String[] args) {
		SpringApplication.run(AemixApplication.class, args);
	}

}
