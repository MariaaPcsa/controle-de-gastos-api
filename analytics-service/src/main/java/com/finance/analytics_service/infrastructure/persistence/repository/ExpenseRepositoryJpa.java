package com.finance.analytics_service.infrastructure.persistence.repository;

import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepositoryJpa
        extends JpaRepository<ExpenseEntity, Long> {

    @Query("""
      SELECT new domain.model.ExpenseSummary(e.category, SUM(e.amount))
      FROM ExpenseEntity e
      WHERE e.userId = :userId
      GROUP BY e.category
    """)
    List<ExpenseSummary> summaryByCategory(Long userId);
}

