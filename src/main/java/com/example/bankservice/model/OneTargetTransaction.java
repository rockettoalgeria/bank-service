package com.example.bankservice.model;

import com.sun.istack.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class OneTargetTransaction {

    @NotNull
    private UUID accountId;

    @NotNull
    private BigDecimal amount;

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
