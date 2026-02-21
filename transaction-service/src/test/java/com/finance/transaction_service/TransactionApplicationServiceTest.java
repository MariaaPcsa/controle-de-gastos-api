package com.finance.transaction_service;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.DeleteTransactionUseCase;
import com.finance.transaction_service.domain.usecase.ListTransactionsUseCase;
import com.finance.transaction_service.infrastructure.external.ExchangeRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionApplicationServiceTest {

    private CreateTransactionUseCase createUseCase;
    private UpdateTransactionUseCase updateUseCase;
    private DeleteTransactionUseCase deleteUseCase;
    private ListTransactionsUseCase listUseCase;
    private ExchangeRateClient exchangeRateClient;

    private TransactionApplicationService service;

    @BeforeEach
    void setup() {
        createUseCase = mock(CreateTransactionUseCase.class);
        updateUseCase = mock(UpdateTransactionUseCase.class);
        deleteUseCase = mock(DeleteTransactionUseCase.class);
        listUseCase = mock(ListTransactionsUseCase.class);
        exchangeRateClient = mock(ExchangeRateClient.class);

        service = new TransactionApplicationService(
                createUseCase,
                updateUseCase,
                deleteUseCase,
                listUseCase,
                exchangeRateClient
        );
    }

    @Test
    void should_create_transaction_successfully() {
        Long userId = 1L;
        String description = "Salário";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String currency = "BRL";
        String category = "Renda";
        TransactionType type = TransactionType.DEPOSIT;

        Transaction transaction = Transaction.create(userId, description, amount, amount, currency, category, type);
        when(createUseCase.execute(any())).thenReturn(transaction);

        Transaction result = service.create(userId, description, amount, currency, category, type);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(amount, result.getAmount());
        assertEquals(type, result.getType());

        verify(createUseCase, times(1)).execute(any());
    }

    @Test
    void should_update_transaction_successfully() {
        UUID transactionId = UUID.randomUUID();
        String description = "Aluguel";
        BigDecimal amount = BigDecimal.valueOf(500);
        BigDecimal originalAmount = BigDecimal.valueOf(500);
        String category = "Despesa";
        String currency = "BRL";
        TransactionType type = TransactionType.WITHDRAW;

        Transaction updated = Transaction.create(1L, description, amount, originalAmount, currency, category, type);

        when(updateUseCase.execute(transactionId, description, amount, originalAmount, category, type))
                .thenReturn(updated);

        Transaction result = service.update(transactionId, description, amount, originalAmount, category, type, currency);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(amount, result.getAmount());
        assertEquals(type, result.getType());

        verify(updateUseCase, times(1))
                .execute(transactionId, description, amount, originalAmount, category, type);
    }

    @Test
    void should_delete_transaction_successfully() {
        UUID transactionId = UUID.randomUUID();

        doNothing().when(deleteUseCase).execute(transactionId);

        assertDoesNotThrow(() -> service.delete(transactionId));

        verify(deleteUseCase, times(1)).execute(transactionId);
    }

    @Test
    void should_list_transactions_successfully() {
        Long userId = 1L;

        Transaction t1 = Transaction.create(userId, "Salário", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), "BRL", "Renda", TransactionType.DEPOSIT);
        Transaction t2 = Transaction.create(userId, "Aluguel", BigDecimal.valueOf(500), BigDecimal.valueOf(500), "BRL", "Despesa", TransactionType.WITHDRAW);

        when(listUseCase.execute(userId)).thenReturn(List.of(t1, t2));

        List<Transaction> result = service.list(userId);

        // ✅ Verifica apenas se ambos estão presentes, sem depender da ordem
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> "Salário".equals(t.getDescription())));
        assertTrue(result.stream().anyMatch(t -> "Aluguel".equals(t.getDescription())));

        verify(listUseCase, times(1)).execute(userId);
    }
}