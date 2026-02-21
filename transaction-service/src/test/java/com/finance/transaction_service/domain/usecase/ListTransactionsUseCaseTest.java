package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ListTransactionsUseCaseTest {

    @Test
    void should_return_list_of_transactions() {
        TransactionRepository repo = mock(TransactionRepository.class);
        ListTransactionsUseCase useCase = new ListTransactionsUseCase(repo);

        Transaction t1 = Transaction.restore(
                java.util.UUID.randomUUID(),
                1L,
                "Salário",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                "BRL",
                "Renda",
                TransactionType.DEPOSIT,
                LocalDateTime.now().minusDays(1) // mais antiga
        );

        Transaction t2 = Transaction.restore(
                java.util.UUID.randomUUID(),
                1L,
                "Aluguel",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(500),
                "BRL",
                "Moradia",
                TransactionType.WITHDRAW,
                LocalDateTime.now() // mais recente
        );

        // Lista mutável para evitar UnsupportedOperationException
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);

        when(repo.findByUserId(1L)).thenReturn(transactions);

        List<Transaction> result = useCase.execute(1L);

        // Ordem decrescente por data
        assertEquals(2, result.size());
        assertEquals("Aluguel", result.get(0).getDescription());
        assertEquals("Salário", result.get(1).getDescription());

        verify(repo, times(1)).findByUserId(1L);
    }
}