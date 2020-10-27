package com.example.bankservice.controller;

import java.util.List;

import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;

	@GetMapping
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@PostMapping
	public Account createAccount(@Validated @RequestBody Account account) throws ResourceNotFoundException {
		String username = account.getUsername();
		List<Account> accountList = accountRepository.findAll();
		for (Account element : accountList) {
			if (element.getUsername().equals(username)) {
				throw new ResourceNotFoundException("Account with this name exists"); // TODO use specific exception
			}
		}
		return accountRepository.save(account);
	}

	@DeleteMapping("{id}")
	public String deleteAccount(@PathVariable(value = "id") Long id)
			throws ResourceNotFoundException {
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found for this id :: " + id)); // TODO use string builder here

		accountRepository.delete(account);
		return "[deleted:" + id + "]"; // TODO use string builder here
	}
}
