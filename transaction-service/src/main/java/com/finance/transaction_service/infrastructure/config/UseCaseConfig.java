package com.finance.transaction_service.infrastructure.config;

import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.DeleteTransactionUseCase;
import com.finance.transaction_service.domain.usecase.ListTransactionsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(TransactionRepository repository) {
        return new CreateTransactionUseCase(repository);
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(TransactionRepository repository) {
        return new UpdateTransactionUseCase(repository);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(TransactionRepository repository) {
        return new DeleteTransactionUseCase(repository);
    }

    @Bean
    public ListTransactionsUseCase listTransactionsUseCase(TransactionRepository repository) {
        return new ListTransactionsUseCase(repository);
    }
}
