package com.finance.transaction_service.infrastructure.persistence.repository;

import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepositoryJpa extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserId(Long userId);
}

