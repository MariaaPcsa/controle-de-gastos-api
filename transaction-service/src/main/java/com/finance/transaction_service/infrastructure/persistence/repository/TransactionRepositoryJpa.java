package com.finance.transaction_service.infrastructure.persistence.repository;

@Repository
public interface TransactionRepositoryJpa
        extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserId(Long userId);
}

