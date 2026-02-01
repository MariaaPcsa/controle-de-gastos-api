package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class DeleteTransactionUseCaseTest {

    @Test
    void should_delete_transaction_successfully() {
        TransactionRepository repo = mock(TransactionRepository.class);
        DeleteTransactionUseCase useCase = new DeleteTransactionUseCase(repo);

        // Transação com ID definido
        Transaction existing = new Transaction(
                1L,       // id
                1L,       // userId
                "Salário",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                "BRL",
                TransactionType.DEPOSIT,
                null
        );

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(repo).deleteById(1L);

        // Executa o use case
        assertDoesNotThrow(() -> useCase.execute(1L));

        // Verifica se deleteById foi chamado
        verify(repo, times(1)).deleteById(1L);
    }

}
