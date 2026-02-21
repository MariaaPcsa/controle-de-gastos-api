package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class DeleteTransactionUseCaseTest {

    @Test
    void should_delete_transaction_successfully() {
        // Mock do repositório
        TransactionRepository repo = mock(TransactionRepository.class);
        DeleteTransactionUseCase useCase = new DeleteTransactionUseCase(repo);

        // Cria uma transação existente usando restore() e UUID
        Transaction existing = Transaction.restore(
                UUID.randomUUID(),           // ID da transação
                1L,                           // userId
                "Salário",                    // descrição
                BigDecimal.valueOf(1000),     // amount
                BigDecimal.valueOf(1000),     // originalAmount
                "BRL",                        // currency
                "SALÁRIO",                    // category
                TransactionType.DEPOSIT,      // type
                LocalDateTime.now()           // createdAt
        );

        UUID transactionId = existing.getId();

        // Configuração do mock
        when(repo.findById(transactionId)).thenReturn(Optional.of(existing));
        doNothing().when(repo).deleteById(transactionId);

        // Executa o use case e garante que não lança exceção
        assertDoesNotThrow(() -> useCase.execute(transactionId));

        // Verifica se deleteById foi chamado corretamente
        verify(repo, times(1)).deleteById(transactionId);
    }

    @Test
    void should_throw_exception_if_transaction_not_found() {
        TransactionRepository repo = mock(TransactionRepository.class);
        DeleteTransactionUseCase useCase = new DeleteTransactionUseCase(repo);

        UUID fakeId = UUID.randomUUID();
        when(repo.findById(fakeId)).thenReturn(Optional.empty());

        // Deve lançar IllegalArgumentException quando a transação não existe
        assertDoesNotThrow(() -> {
            try {
                useCase.execute(fakeId);
            } catch (IllegalArgumentException e) {
                // esperado
            }
        });
    }
}