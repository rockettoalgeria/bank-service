package com.example.bankservice.service;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.InvalidTransactionRequestException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.model.Transaction;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.RollbackException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public void performWriteOff(Transaction transaction, UUID accountId)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        BigDecimal amount = transaction.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new InvalidTransactionAmountException();
        }
        Account account = accountRepository.findOneById(accountId);
        if (account == null) {
            throw new ResourceNotFoundException();
        }

        BigDecimal potentialBalance = account.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        if (potentialBalance.compareTo(BigDecimal.ZERO) >= 0) {
            account.setBalance(potentialBalance);
        } else {
            throw new InvalidTransactionAmountException();
        }
    }

    public void performReplenishment(Transaction transaction, UUID accountId)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        BigDecimal amount = transaction.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new InvalidTransactionAmountException();
        }
        Account account = accountRepository.findOneById(accountId);
        if (account == null) {
            throw new ResourceNotFoundException();
        }

        BigDecimal newBalance = account.getBalance().add(amount).setScale(2, RoundingMode.HALF_EVEN);
        account.setBalance(newBalance);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor= RollbackException.class)
    public void doWithdrawTransaction(Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() != null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        performWriteOff(transaction, transaction.getFromAccountId());
        transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    public void doDepositTransaction(Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() != null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        performReplenishment(transaction, transaction.getFromAccountId());
        transactionRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor=Exception.class)
    public void doTransferBetweenAccounts(Transaction transaction)
            throws InvalidTransactionAmountException, InvalidTransactionRequestException, ResourceNotFoundException {
        if (transaction.getToAccountId() == null || transaction.getFromAccountId() == null) {
            throw new InvalidTransactionRequestException();
        }
        performWriteOff(transaction, transaction.getFromAccountId());
        performReplenishment(transaction, transaction.getToAccountId());
        transactionRepository.save(transaction);
    }
}
