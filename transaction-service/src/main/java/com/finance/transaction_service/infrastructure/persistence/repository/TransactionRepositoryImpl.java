package com.finance.transaction_service.infrastructure.persistence.repository;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;
import com.finance.transaction_service.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionRepositoryJpa jpaRepository;

    public TransactionRepositoryImpl(TransactionRepositoryJpa jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionMapper.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return TransactionMapper.toDomain(saved);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByUserIdAndPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByUserIdAndCreatedAtBetween(userId, start, end)
                .stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    // ✅ Mudou de Long para UUID
    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(TransactionMapper::toDomain);
    }

    // ✅ Mudou de Long para UUID
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    // ✅ Mudou de Long para UUID
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}