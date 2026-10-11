package com.fincore.api.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;


    public void update(String name, AccountType type) {
        this.name = name;
        this.type = type;
    }


    protected Account() {
        // Construtor utilizado pelo Hibernate
    }

    public Account(UUID id, String name, AccountType type, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
