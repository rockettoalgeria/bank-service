package com.example.bankservice.service;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.model.OneTargetTransaction;
import com.example.bankservice.model.TransferTransaction;
import com.example.bankservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    private void performWriteOff(BigDecimal amount, UUID accountId)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new InvalidTransactionAmountException();
        }
        Account account = accountRepository.findOneById(accountId);
        if (account == null) {
            throw new ResourceNotFoundException();
        }

        BigDecimal potentialBalance = account.getBalance()
                                                .subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        if (potentialBalance.compareTo(BigDecimal.ZERO) >= 0) {
            account.setBalance(potentialBalance);
        } else {
            throw new InvalidTransactionAmountException();
        }
    }

    private void performReplenishment(BigDecimal amount, UUID accountId)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new InvalidTransactionAmountException();
        }
        Account account = accountRepository.findOneById(accountId);
        if (account == null) {
            throw new ResourceNotFoundException();
        }

        BigDecimal newBalance = account.getBalance()
                                        .add(amount).setScale(2, RoundingMode.HALF_EVEN);
        account.setBalance(newBalance);
    }

    @Transactional
    public void doWithdrawTransaction(OneTargetTransaction oneTargetTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        performWriteOff(oneTargetTransaction.getAmount(), oneTargetTransaction.getAccountId());
    }

    @Transactional
    public void doDepositTransaction(OneTargetTransaction oneTargetTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        performReplenishment(oneTargetTransaction.getAmount(), oneTargetTransaction.getAccountId());
    }

    @Transactional
    synchronized public void doTransferBetweenAccounts(TransferTransaction transferTransaction)
            throws InvalidTransactionAmountException, ResourceNotFoundException {
        performWriteOff(transferTransaction.getAmount(), transferTransaction.getFromAccountId());
        performReplenishment(transferTransaction.getAmount(), transferTransaction.getToAccountId());
    }
}
