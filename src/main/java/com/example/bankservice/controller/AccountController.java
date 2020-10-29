package com.example.bankservice.controller;

import java.math.BigDecimal;

import com.example.bankservice.model.Account;
import com.example.bankservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public Account createAccount() {
		Account account = new Account();
		account.setBalance(BigDecimal.valueOf(0));
		return accountRepository.save(account);
	}
}
