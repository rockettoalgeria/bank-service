package com.example.bankservice.controller;

import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Transaction;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    @PostMapping("/withdraw")
    public Transaction withdrawTransaction(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        transactionService.performWriteOff(transaction, transaction.getFromAccountId());
        return transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    @PostMapping("/deposit")
    public Transaction depositTransaction(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        transactionService.performReplenishment(transaction, transaction.getFromAccountId());
        return transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    @PostMapping("/transfer")
    public Transaction transferBetweenAccounts(@Validated @RequestBody Transaction transaction) throws ResourceNotFoundException {
        transactionService.performWriteOff(transaction, transaction.getFromAccountId());
        transactionService.performReplenishment(transaction, transaction.getToAccountId());
        return transactionRepository.save(transaction);
    }
}