package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ListTransactionsUseCaseTest {

    @Test
    void should_return_list_of_transactions() {
        TransactionRepository repo = mock(TransactionRepository.class);
        ListTransactionsUseCase useCase = new ListTransactionsUseCase(repo);

        // Criação de transações
        Transaction t1 = Transaction.create(1L, "Salário", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), "BRL", TransactionType.DEPOSIT);
        Transaction t2 = Transaction.create(1L, "Aluguel", BigDecimal.valueOf(500), BigDecimal.valueOf(500), "BRL", TransactionType.WITHDRAW);

        // Mock retorna lista mutável para evitar UnsupportedOperationException
        when(repo.findByUserId(1L)).thenReturn(new ArrayList<>(List.of(t1, t2)));

        // Executa o use case
        List<Transaction> result = useCase.execute(1L);

        // Verificações
        assertEquals(2, result.size());
        assertEquals("Salário", result.get(0).getDescription());
        assertEquals("Aluguel", result.get(1).getDescription());

        verify(repo, times(1)).findByUserId(1L);
    }
}