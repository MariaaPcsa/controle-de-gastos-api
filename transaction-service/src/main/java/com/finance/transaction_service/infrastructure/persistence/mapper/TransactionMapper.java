package com.finance.transaction_service.infrastructure.persistence.mapper;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionMapper {

    // Converte de Entity para Domain
    public static Transaction toDomain(TransactionEntity e) {
        if (e == null) return null;

        return new Transaction(
                e.getId(),                 // id
                e.getUserId(),             // userId
                e.getDescription(),        // description
                e.getAmount(),             // amount (convertido)
                e.getAmount(),             // originalAmount (se não tiver outro campo separado)
                e.getCurrency(),           // currency
                e.getType(),               // TransactionType
                e.getCreatedAt() != null ? e.getCreatedAt() : LocalDateTime.now() // createdAt
        );
    }

    // Converte de Domain para Entity
    public static TransactionEntity toEntity(Transaction t) {
        if (t == null) return null;

        TransactionEntity e = new TransactionEntity();
        e.setId(t.getId());
        e.setUserId(t.getUserId());
        e.setDescription(t.getDescription());
        e.setAmount(t.getAmount());
        e.setCurrency(t.getCurrency());
        e.setCategory(null);
        e.setType(t.getType());
        e.setCreatedAt(t.getCreatedAt() != null ? t.getCreatedAt() : LocalDateTime.now());
        return e;
    }
}
