package com.finance.transaction_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private Long userId;
    private String description;
    private BigDecimal amount;
    private BigDecimal originalAmount;
    private String currency;
    private String category;
    private TransactionType type;
    private LocalDateTime createdAt;

    private Transaction(UUID id,
                        Long userId,
                        String description,
                        BigDecimal amount,
                        BigDecimal originalAmount,
                        String currency,
                        String category,
                        TransactionType type,
                        LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.originalAmount = originalAmount;
        this.currency = currency;
        this.category = category;
        this.type = type;
        this.createdAt = createdAt;
    }

    // CREATE (novo registro)
    public static Transaction create(Long userId,
                                     String description,
                                     BigDecimal amount,
                                     BigDecimal originalAmount,
                                     String currency,
                                     String category,
                                     TransactionType type) {

        return new Transaction(
                UUID.randomUUID(),
                userId,
                description,
                amount,
                originalAmount,
                currency,
                category,
                type,
                LocalDateTime.now()
        );
    }

    // RESTORE (vindo do banco)
    public static Transaction restore(UUID id,
                                      Long userId,
                                      String description,
                                      BigDecimal amount,
                                      BigDecimal originalAmount,
                                      String currency,
                                      String category,
                                      TransactionType type,
                                      LocalDateTime createdAt) {

        return new Transaction(
                id,
                userId,
                description,
                amount,
                originalAmount,
                currency,
                category,
                type,
                createdAt
        );
    }

    // UPDATE (retornando a própria entidade)
    public Transaction update(String description,
                              BigDecimal amount,
                              BigDecimal originalAmount,
                              String category,
                              TransactionType type) {

        this.description = description;
        this.amount = amount;
        this.originalAmount = originalAmount;
        this.category = category;
        this.type = type;

        return this;
    }

    // GETTERS

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public TransactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public BigDecimal getSignedAmount() {
        return type.isExpense() ? amount.negate() : amount;
    }
}