package com.example.bankservice;

import com.example.bankservice.exception.InvalidTransactionAmountException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.model.Account;
import com.example.bankservice.model.OneTargetTransaction;
import com.example.bankservice.model.TransferTransaction;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BankServiceTests {

    private final String authUrl = "http://localhost:8080/auth";
    private final String createAccountUrl = "http://localhost:8080/account";
    private final String transactionUrl = "http://localhost:8080/transaction/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    private String getToken() throws Exception {
        int tokenSequenceStart = 10;
        int tokenSequenceEnd = 184; // do something with that horrible string processing
        String requestBody = "{\"username\":\"admin\",\"password\":\"password\"}";

        MockHttpServletRequestBuilder authRequest = post(authUrl).contentType("application/json").content(requestBody);

        MockHttpServletResponse ret = mockMvc.perform(authRequest)
                .andReturn()
                .getResponse();

        return "Bearer " + ret.getContentAsString()
                .substring(tokenSequenceStart, tokenSequenceEnd);
    }

    private String buildSimpleRequestWithValue(BigDecimal value, UUID uuid) {
        StringBuilder requestSB = new StringBuilder();
        requestSB.append("{\"accountId\":\"")
                .append(uuid.toString())
                .append("\",\"amount\":")
                .append(value).append("}");
        return requestSB.toString();
    }

    private String buildTransferRequestWithValue(BigDecimal value, UUID from, UUID to) {
        StringBuilder requestSB = new StringBuilder();
        requestSB.append("{\"fromAccountId\":\"")
                .append(from.toString())
                .append("\",\"toAccountId\":\"")
                .append(to.toString())
                .append("\",\"amount\":")
                .append(value).append("}");
        return requestSB.toString();
    }

    private void performSimpleTransaction(BigDecimal value, UUID accountID, String token, String type, boolean valid) throws Exception {
        MockHttpServletRequestBuilder request = post(transactionUrl + type)
                .header("Authorization" , token)
                .contentType("application/json")
                .content(buildSimpleRequestWithValue(value, accountID));

        if (valid) {
            mockMvc.perform(request)
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    private void performTransferTransaction(BigDecimal value, UUID from, UUID to, String token) throws Exception {
        MockHttpServletRequestBuilder request = post(transactionUrl + "/transfer")
                .header("Authorization" , token)
                .contentType("application/json")
                .content(buildTransferRequestWithValue(value, from, to));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    private UUID createAccount(String token) throws Exception {
        MockHttpServletRequestBuilder accountCreateRequest = post(createAccountUrl).header("Authorization" , token);
        String ret = mockMvc.perform(accountCreateRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        Account account = new ObjectMapper().readValue(ret, Account.class);

        return account.getId();
    }

    private BigDecimal getActualBalance(UUID accountID) { return accountRepository.findOneById(accountID).getBalance(); }

    @Test
    void calculations() throws Exception {
        String token = getToken();

        UUID accountID = createAccount(token);
        OneTargetTransaction oneTargetTransaction = new OneTargetTransaction();

        oneTargetTransaction.setAccountId(accountID);

        oneTargetTransaction.setAmount(BigDecimal.valueOf(0.01));
        transactionService.doDepositTransaction(oneTargetTransaction);
        Assert.assertEquals(BigDecimal.valueOf(0.01), getActualBalance(accountID));

        oneTargetTransaction.setAmount(BigDecimal.valueOf(0.0156));
        transactionService.doDepositTransaction(oneTargetTransaction);
        Assert.assertEquals(BigDecimal.valueOf(0.03), getActualBalance(accountID));

        oneTargetTransaction.setAmount(BigDecimal.valueOf(0.125));
        transactionService.doDepositTransaction(oneTargetTransaction);
        Assert.assertEquals(BigDecimal.valueOf(0.16), getActualBalance(accountID));

        oneTargetTransaction.setAmount(BigDecimal.valueOf(Long.MAX_VALUE));
        transactionService.doDepositTransaction(oneTargetTransaction);
        Assert.assertEquals(BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(0.16)), getActualBalance(accountID));
    }

    @Test
    void transactionValidation() throws Exception {
        String token = getToken();

        UUID accountID_1 = createAccount(token);
        UUID accountID_2 = createAccount(token);

        performSimpleTransaction(BigDecimal.valueOf(1000), accountID_1, token, "deposit", true);
        performSimpleTransaction(BigDecimal.valueOf(555.55), accountID_2, token, "deposit",true);
        performTransferTransaction(BigDecimal.valueOf(155.54), accountID_2, accountID_1, token);
        Assert.assertEquals(BigDecimal.valueOf(1155.54), getActualBalance(accountID_1));
        Assert.assertEquals(BigDecimal.valueOf(400.01), getActualBalance(accountID_2));

        performSimpleTransaction(BigDecimal.valueOf(0.01), accountID_2, token, "withdraw", true);
        Assert.assertEquals(BigDecimal.valueOf(400.00).setScale(2, RoundingMode.HALF_EVEN), getActualBalance(accountID_2));

        performSimpleTransaction(BigDecimal.valueOf(0.001), accountID_2, token, "withdraw", false);
        performSimpleTransaction(BigDecimal.valueOf(-5), accountID_2, token, "withdraw", false);
        performSimpleTransaction(BigDecimal.valueOf(10000), accountID_2, token, "withdraw", false);
        performSimpleTransaction(BigDecimal.valueOf(0), accountID_2, token, "withdraw", false);
    }

    @Test
    void loopDeposit() throws Exception {
        String token = getToken();

        UUID accountID = createAccount(token);
        OneTargetTransaction oneTargetTransaction = new OneTargetTransaction();

        oneTargetTransaction.setAccountId(accountID);

        oneTargetTransaction.setAmount(BigDecimal.valueOf(0.9));
        for (int i = 0; i < 10000; i++) {
            transactionService.doDepositTransaction(oneTargetTransaction);
        }
    }

    @Test
    void unathorized() throws Exception {
        MockHttpServletRequestBuilder request = post(createAccountUrl)
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void multithreading() throws Exception {
        String token = getToken();
        UUID accountID_1 = createAccount(token);
        UUID accountID_2 = createAccount(token);

        // init 2 accounts with 1000
        OneTargetTransaction oneTargetTransaction = new OneTargetTransaction();
        oneTargetTransaction.setAccountId(accountID_2);
        oneTargetTransaction.setAmount(BigDecimal.valueOf(1000));
        transactionService.doDepositTransaction(oneTargetTransaction);
        oneTargetTransaction.setAccountId(accountID_1);
        oneTargetTransaction.setAmount(BigDecimal.valueOf(1000));
        transactionService.doDepositTransaction(oneTargetTransaction);

        // deposits to same account with 2 threads
        oneTargetTransaction.setAmount(BigDecimal.valueOf(10));
        Thread thread_1 = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                try {
                    transactionService.doDepositTransaction(oneTargetTransaction);
                } catch (InvalidTransactionAmountException | ResourceNotFoundException e) {
                    e.printStackTrace();
            }
        });

        Thread thread_2 = new Thread(() -> {
            for (int i = 0; i < 5; i++)
                try {
                    transactionService.doDepositTransaction(oneTargetTransaction);
                } catch (InvalidTransactionAmountException | ResourceNotFoundException e) {
                    e.printStackTrace();
            }
        });

        thread_1.start();
        thread_2.start();

        thread_1.join();
        thread_2.join();
        Assert.assertEquals(BigDecimal.valueOf(1150).setScale(2, RoundingMode.HALF_EVEN), getActualBalance(accountID_1));

        // deadlock case
        TransferTransaction transferTransaction = new TransferTransaction();
        transferTransaction.setFromAccountId(accountID_1);
        transferTransaction.setToAccountId(accountID_2);
        transferTransaction.setAmount(BigDecimal.valueOf(33));

        TransferTransaction concurrentTransferTransaction = new TransferTransaction();
        concurrentTransferTransaction.setFromAccountId(accountID_2);
        concurrentTransferTransaction.setToAccountId(accountID_1);
        concurrentTransferTransaction.setAmount(BigDecimal.valueOf(55));

        Thread threadTransfer = new Thread(() -> {
            try {
                transactionService.doTransferBetweenAccounts(transferTransaction);
            } catch (InvalidTransactionAmountException | ResourceNotFoundException e) {
                e.printStackTrace();
            }
        });

        Thread threadConcurrent = new Thread(() -> {
            try {
                transactionService.doTransferBetweenAccounts(concurrentTransferTransaction);
            } catch (InvalidTransactionAmountException | ResourceNotFoundException e) {
                e.printStackTrace();
            }
        });

        threadTransfer.start();
        threadConcurrent.start();

        threadTransfer.join();
        threadConcurrent.join();

        Assert.assertEquals(BigDecimal.valueOf(978).setScale(2, RoundingMode.HALF_EVEN), getActualBalance(accountID_2));
        Assert.assertEquals(BigDecimal.valueOf(1172).setScale(2, RoundingMode.HALF_EVEN), getActualBalance(accountID_1));
    }
}
