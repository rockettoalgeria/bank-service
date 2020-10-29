package com.example.bankservice.controller;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.InvalidTransactionRequestException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Transaction;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=RollbackException.class)
    @PostMapping("/withdraw")
    public void withdrawTransaction(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() != null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        transactionService.performWriteOff(transaction, transaction.getFromAccountId());
        transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    @PostMapping("/deposit")
    public void depositTransaction(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() != null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        transactionService.performReplenishment(transaction, transaction.getFromAccountId());
        transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    @PostMapping("/transfer")
    public void transferBetweenAccounts(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() == null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        transactionService.performWriteOff(transaction, transaction.getFromAccountId());
        transactionService.performReplenishment(transaction, transaction.getToAccountId());
        transactionRepository.save(transaction);
    }
}