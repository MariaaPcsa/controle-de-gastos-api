package com.finance.transaction_service.infrastructure.persistence.repository;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;
import com.finance.transaction_service.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class TransactionRepositoryImpl implements TransactionRepository {

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
    public Optional<Transaction> findById(Long id) {
        return jpaRepository.findById(id)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
