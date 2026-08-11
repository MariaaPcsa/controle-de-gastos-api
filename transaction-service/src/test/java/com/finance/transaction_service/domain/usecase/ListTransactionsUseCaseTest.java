package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ListTransactionsUseCaseTest {

    @Test
    void should_return_list_of_transactions() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        ListTransactionsUseCase useCase =
                new ListTransactionsUseCase(repo);

        UUID userId =
                UUID.randomUUID();

        // =====================================================
        // TRANSAÇÃO 1 - MAIS ANTIGA
        // =====================================================

        Transaction t1 =
                Transaction.restore(
                        UUID.randomUUID(),
                        userId,
                        "Salário",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(1000),
                        "BRL",
                        "Renda",
                        TransactionType.DEPOSIT,
                        LocalDateTime.now().minusDays(1)
                );

        // =====================================================
        // TRANSAÇÃO 2 - MAIS RECENTE
        // =====================================================

        Transaction t2 =
                Transaction.restore(
                        UUID.randomUUID(),
                        userId,
                        "Aluguel",
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(500),
                        "BRL",
                        "Moradia",
                        TransactionType.WITHDRAW,
                        LocalDateTime.now()
                );

        // =====================================================
        // LISTA MUTÁVEL
        // =====================================================

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(t1);
        transactions.add(t2);

        // =====================================================
        // MOCK
        // =====================================================

        when(repo.findByUserId(userId))
                .thenReturn(transactions);

        // =====================================================
        // ACT
        // =====================================================

        List<Transaction> result =
                useCase.execute(userId);

        // =====================================================
        // ASSERT
        // =====================================================

        assertEquals(
                2,
                result.size()
        );

        // Mais recente primeiro
        assertEquals(
                "Aluguel",
                result.get(0).getDescription()
        );

        assertEquals(
                "Salário",
                result.get(1).getDescription()
        );

        // =====================================================
        // VERIFY
        // =====================================================

        verify(
                repo,
                times(1)
        ).findByUserId(userId);
    }
}