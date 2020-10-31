package com.example.bankservice.controller;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.InvalidTransactionRequestException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Transaction;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @PostMapping("/withdraw")
    public void withdrawTransaction(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        transactionService.doWithdrawTransaction(transaction);
    }

    @PostMapping("/deposit")
    public void depositTransaction(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        transactionService.doDepositTransaction(transaction);
    }

    @PostMapping("/transfer")
    public void transferBetweenAccounts(@Validated @RequestBody Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        transactionService.doTransferBetweenAccounts(transaction);
    }
}