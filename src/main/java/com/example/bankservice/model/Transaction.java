package com.example.bankservice.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID fromAccountId;
    private UUID toAccountId = null;
    private BigDecimal amount;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "from_account_id", nullable = false)
    public UUID getFromAccountId() {
        return fromAccountId;
    }
    public void setFromAccountId(UUID from) {
        this.fromAccountId = from;
    }

    @Column(name = "to_account_id", nullable = true)
    public UUID getToAccountId() {
        return toAccountId;
    }
    public void setToAccountId(UUID to) { this.toAccountId = to; }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
