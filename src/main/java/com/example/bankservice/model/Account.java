package com.example.bankservice.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private BigDecimal balance = new BigDecimal(0);

	public UUID getId() { return id; }

	@Column(name = "balance", nullable = false)
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
