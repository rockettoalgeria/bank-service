package com.example.bankservice.service;

import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.model.Transaction;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    public Transaction performWithdraw(Transaction transaction) throws ResourceNotFoundException {
        BigDecimal amount = transaction.getAmount();
        long accountId = transaction.getFromAccountId();

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new WebApplicationException("Invalid withdraw amount", Response.Status.BAD_REQUEST);
        }
        Account account = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this id :: " + accountId));
        BigDecimal potentialBalance = account.getBalance().subtract(amount);
        if (potentialBalance.compareTo(BigDecimal.ZERO) >= 0) {
            account.setBalance(potentialBalance);
        }
        return transactionRepository.save(transaction);
    }
}
