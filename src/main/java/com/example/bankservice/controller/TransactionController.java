package com.example.bankservice.controller;

import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.model.Transaction;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping
    public List<Transaction> getTransactionsList() {
        return transactionRepository.findAll();
    }

    //TODO transactional? how to test?

    @PostMapping("/withdraw")
    public Transaction withdrawTransaction(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        transactionService.performWriteOff(transaction, transaction.getFromAccountId()); // check account id here
        return transactionRepository.save(transaction);
    }

    @PostMapping("/deposit")
    public Transaction depositTransaction(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        transactionService.performReplenishment(transaction, transaction.getFromAccountId()); // check account id here
        return transactionRepository.save(transaction);
    }

    @PostMapping("/transfer")
    public Transaction transferBetweenAccounts(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException { // how to rollback correct?
        transactionService.performWriteOff(transaction, transaction.getFromAccountId()); // check account id here
        transactionService.performReplenishment(transaction, transaction.getToAccountId()); // check account id here
        return transactionRepository.save(transaction);
    }
}