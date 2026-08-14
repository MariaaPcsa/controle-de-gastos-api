package com.finance.analytics_service.domain.usecase;



import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;

public interface ProcessTransactionUseCase {

    void process(ExpenseEntity expense);

}

