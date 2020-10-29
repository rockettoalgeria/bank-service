package com.example.bankservice;

import com.example.bankservice.model.Account;
import com.example.bankservice.repository.AccountRepository;
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
        requestSB.append("{\"fromAccountId\":\"")
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
        performSimpleTransaction(BigDecimal.valueOf(0.01), accountID, token, "deposit", true);
        Assert.assertEquals(BigDecimal.valueOf(0.01), getActualBalance(accountID));

        performSimpleTransaction(BigDecimal.valueOf(0.0156), accountID, token, "deposit",true);
        Assert.assertEquals(BigDecimal.valueOf(0.03), getActualBalance(accountID));

        performSimpleTransaction(BigDecimal.valueOf(0.125), accountID, token, "deposit", true);
        Assert.assertEquals(BigDecimal.valueOf(0.16), getActualBalance(accountID));

        performSimpleTransaction(BigDecimal.valueOf(Long.MAX_VALUE), accountID, token, "deposit", true);
        Assert.assertEquals(BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(0.16)), getActualBalance(accountID));
    }

    @Test
    void transactionValidation() throws Exception {
        String token = getToken();

        UUID accountID_1 = createAccount(token);
        UUID accountID_2 = createAccount(token);

        performSimpleTransaction(BigDecimal.valueOf(1000), accountID_1, token, "deposit", true);
        performSimpleTransaction(BigDecimal.valueOf(555.55), accountID_2, token, "deposit", true);
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

        for (int i = 0; i < 10000; i++) {
            performSimpleTransaction(BigDecimal.valueOf(0.9), accountID, token, "deposit", true);
        }
    }

    @Test
    void unathorized() throws Exception {
        MockHttpServletRequestBuilder request = post(createAccountUrl)
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
