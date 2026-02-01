package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateTransactionUseCaseTest {

    @Test
    void should_update_transaction_successfully() {
        // Mock do repositório
        TransactionRepository repo = mock(TransactionRepository.class);
        UpdateTransactionUseCase useCase = new UpdateTransactionUseCase(repo);

        // Transação existente
        Transaction existing = Transaction.create(
                1L,
                "Salário",
                BigDecimal.valueOf(1000),   // valor convertido em BRL
                BigDecimal.valueOf(1000),   // valor original
                "BRL",
                TransactionType.DEPOSIT
        );

        // Transação atualizada
        Transaction updated = existing.update(
                "Salário Atualizado",
                BigDecimal.valueOf(1200),   // valor convertido
                BigDecimal.valueOf(1200),   // valor original
                "BRL",
                TransactionType.DEPOSIT
        );

        // Configuração do mock
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(updated);

        // Executa o use case com todos os argumentos necessários
        Transaction result = useCase.execute(
                1L,
                "Salário Atualizado",
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(1200),
                "BRL",
                TransactionType.DEPOSIT
        );

        // Verificações
        assertEquals(BigDecimal.valueOf(1200), result.getAmount());
        assertEquals("Salário Atualizado", result.getDescription());
        assertEquals("BRL", result.getCurrency());
        assertEquals(TransactionType.DEPOSIT, result.getType());

        // Verifica se o save foi chamado uma vez
        verify(repo, times(1)).save(updated);
    }

    @Test
    void should_throw_exception_if_transaction_not_found() {
        TransactionRepository repo = mock(TransactionRepository.class);
        UpdateTransactionUseCase useCase = new UpdateTransactionUseCase(repo);

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                useCase.execute(
                        1L,
                        "Nova Descrição",
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100),
                        "BRL",
                        TransactionType.DEPOSIT
                )
        );
    }
}



