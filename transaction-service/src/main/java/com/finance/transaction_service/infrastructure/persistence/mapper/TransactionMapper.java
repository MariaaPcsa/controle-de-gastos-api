package com.finance.transaction_service.infrastructure.persistence.mapper;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;

import java.time.LocalDateTime;

public class TransactionMapper {

    // Converte Entity → Domain
    public static Transaction toDomain(TransactionEntity e) {
        if (e == null) return null;

        // restore é método privado que aceita UUID
        return Transaction.restore(
                e.getId(),          // UUID
                e.getUserId(),
                e.getDescription(),
                e.getAmount(),
                e.getOriginalAmount(), // corrigido
                e.getCurrency(),
                e.getCategory(),
                e.getType(),
                e.getCreatedAt() != null ? e.getCreatedAt() : LocalDateTime.now()
        );
    }

    // Converte Domain → Entity
    public static TransactionEntity toEntity(Transaction t) {
        if (t == null) return null;

        TransactionEntity e = new TransactionEntity();
        e.setId(t.getId());           // agora UUID
        e.setUserId(t.getUserId());
        e.setDescription(t.getDescription());
        e.setAmount(t.getAmount());
        e.setOriginalAmount(t.getOriginalAmount()); // corrigido
        e.setCurrency(t.getCurrency());
        e.setCategory(t.getCategory());
        e.setType(t.getType());
        e.setCreatedAt(t.getCreatedAt() != null ? t.getCreatedAt() : LocalDateTime.now());

        return e;
    }
}