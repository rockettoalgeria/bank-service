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

    public void performWriteOff(Transaction transaction, long accountId) throws ResourceNotFoundException { //rename, othr exceptions
        BigDecimal amount = transaction.getAmount();        //TODO Also check optional attributes (to/from)

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new WebApplicationException("Invalid withdraw amount", Response.Status.BAD_REQUEST);
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this id :: " + accountId));
        BigDecimal potentialBalance = account.getBalance().subtract(amount);
        if (potentialBalance.compareTo(BigDecimal.ZERO) >= 0) {
            System.out.print(potentialBalance);
            account.setBalance(potentialBalance);
        }
    }

    public void performReplenishment(Transaction transaction, long accountId) throws ResourceNotFoundException {
        BigDecimal amount = transaction.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new WebApplicationException("Invalid withdraw amount", Response.Status.BAD_REQUEST); // dup code here
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this id :: " + accountId));
        BigDecimal newBalance = account.getBalance().add(amount);
        System.out.println(" -> " + newBalance);
        System.out.println(amount + " | " + account.getBalance());
        account.setBalance(newBalance);
    }
}
