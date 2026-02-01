package com.finance.analytics_service.infrastructure.persistence.repository;



import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.domain.repository.ExpenseRepository;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface ExpenseRepositoryJpa extends JpaRepository<ExpenseEntity, Long>, ExpenseRepository {

    List<ExpenseEntity> findByUserId(Long userId);

    @Override
    default ExpenseSummary getSummaryByUser(Long userId) {
        List<ExpenseEntity> expenses = findByUserId(userId);

        BigDecimal totalMes = expenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAno = totalMes;

        Map<String, BigDecimal> porCategoria = expenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseEntity::getCategory,
                        Collectors.mapping(
                                ExpenseEntity::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        return new ExpenseSummary(totalMes, totalAno, porCategoria);
    }
}

