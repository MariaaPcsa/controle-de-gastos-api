package com.finance.transaction_service.domain.model;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private String category;
    private LocalDateTime createdAt;

    public Transaction(Long userId, BigDecimal amount, String currency,
                       TransactionType type, String category) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    // getters
}
