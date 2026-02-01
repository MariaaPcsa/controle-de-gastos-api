package com.finance.transaction_service.presentation.dto;

import com.finance.transaction_service.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private BigDecimal amount;         // convertido
    private BigDecimal originalAmount; // original
    private String currency;
    private String category;
    private String type;
    private String typeDescription;
    private BigDecimal signedAmount;
    private LocalDateTime createdAt;

    public static TransactionResponseDTO fromDomain(Transaction t) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.id = t.getId();
        dto.amount = t.getAmount();
        dto.originalAmount = t.getOriginalAmount();
        dto.currency = t.getCurrency();
        dto.category = t.getDescription(); // ou category separado
        dto.type = t.getType().name();
        dto.typeDescription = t.getType().getDescription();
        dto.signedAmount = t.getSignedAmount();
        dto.createdAt = t.getCreatedAt();
        return dto;
    }

    // Getters
    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getTypeDescription() { return typeDescription; }
    public BigDecimal getSignedAmount() { return signedAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
