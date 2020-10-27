package com.example.bankservice.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {

    private long id;
    private long fromAccountId;
    private long toAccountId;
    private BigDecimal amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "fromAccountId", nullable = false)
    public long getFromAccountId() {
        return fromAccountId;
    }
    public void setFromAccountId(long from) {
        this.fromAccountId = from;
    }

    @Column(name = "toAccountId", nullable = true)
    public long getToAccountId() {
        return toAccountId;
    }
    public void setToAccountId(long to) {
        this.toAccountId = to;
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
