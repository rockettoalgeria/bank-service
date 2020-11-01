package com.example.bankservice.controller;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.OneTargetTransaction;
import com.example.bankservice.model.TransferTransaction;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/withdraw")
    public void withdrawTransaction(@Validated @RequestBody OneTargetTransaction oneTargetTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        transactionService.doWithdrawTransaction(oneTargetTransaction);
    }

    @PostMapping("/deposit")
    public void depositTransaction(@Validated @RequestBody OneTargetTransaction oneTargetTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        transactionService.doDepositTransaction(oneTargetTransaction);
    }

    @PostMapping("/transfer")
    public void transferBetweenAccounts(@Validated @RequestBody TransferTransaction transferTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        transactionService.doTransferBetweenAccounts(transferTransaction);
    }
}