package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

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
                "SALÁRIO",
                TransactionType.DEPOSIT
        );

        UUID transactionId = existing.getId();

        // Transação atualizada
        Transaction updated = existing.update(
                "Salário Atualizado",
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(1200),
                "SALÁRIO",
                TransactionType.DEPOSIT
        );

        // Configuração do mock (agora usando UUID)
        when(repo.findById(transactionId)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(updated);

        // Executa o use case
        Transaction result = useCase.execute(
                transactionId,
                "Salário Atualizado",
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(1200),
                "SALÁRIO",
                TransactionType.DEPOSIT
        );

        // Verificações
        assertEquals(BigDecimal.valueOf(1200), result.getAmount());
        assertEquals("Salário Atualizado", result.getDescription());
        assertEquals("SALÁRIO", result.getCategory());
        assertEquals(TransactionType.DEPOSIT, result.getType());

        // Verifica se o save foi chamado uma vez
        verify(repo, times(1)).save(updated);
    }

    @Test
    void should_throw_exception_if_transaction_not_found() {
        TransactionRepository repo = mock(TransactionRepository.class);
        UpdateTransactionUseCase useCase = new UpdateTransactionUseCase(repo);

        UUID fakeId = UUID.randomUUID();
        when(repo.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                useCase.execute(
                        fakeId,
                        "Nova Descrição",
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100),
                        "MISC",
                        TransactionType.DEPOSIT
                )
        );
    }
}