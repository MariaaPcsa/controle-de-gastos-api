package com.finance.transaction_service.infrastructure.persistence.entity;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id @GeneratedValue
    private Long id;

    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String category;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime createdAt;
}

