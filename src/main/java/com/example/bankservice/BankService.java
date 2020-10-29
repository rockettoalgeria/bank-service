package com.example.bankservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.jdbc.Sql;

@SpringBootApplication
@EnableJpaAuditing
@Sql({"/schema.sql"})
public class BankService {

	public static void main(String[] args) {
		SpringApplication.run(BankService.class, args);
	}
}
