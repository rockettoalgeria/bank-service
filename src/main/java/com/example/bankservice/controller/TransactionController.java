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

    @GetMapping
    public List<Transaction> getTransactionsList() {
        return transactionService.getAllTransactions();
    }

    //TODO transactional? how to test?

    @PostMapping("/withdraw")
    public Transaction withdrawTransaction(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        return transactionService.performWithdraw(transaction);
    }

    /*@PostMapping("/deposit")
    public Transaction depositTransaction(@Validated @RequestBody Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    @PostMapping("/transfer")
    public Transaction transferBetweenAccounts(@Validated @RequestBody Transaction transaction) {
        if (transaction.amount.equals(BigDecimal.ZERO)){ // how to  <= ??
            throw new WebApplicationException("Invalid withdraw amount", Response.Status.BAD_REQUEST);
        }
        return transactionRepository.save(transaction);
    }*/
}