package com.finance.transaction_service.infrastructure.persistence.mapper;

public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity e) {
        return new Transaction(
                e.getUserId(),
                e.getAmount(),
                e.getCurrency(),
                e.getType(),
                e.getCategory()
        );
    }

    public static TransactionEntity toEntity(Transaction t) {
        TransactionEntity e = new TransactionEntity();
        e.setUserId(t.getUserId());
        e.setAmount(t.getAmount());
        e.setCurrency(t.getCurrency());
        e.setType(t.getType());
        e.setCategory(t.getCategory());
        e.setCreatedAt(t.getCreatedAt());
        return e;
    }
}
