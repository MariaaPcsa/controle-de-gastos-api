package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetTransactionByIdUseCaseTest {

    @Test
    void should_return_transaction_when_user_is_owner() {

        TransactionRepository repo = mock(TransactionRepository.class);
        GetTransactionByIdUseCase useCase = new GetTransactionByIdUseCase(repo);

        UUID userId = UUID.randomUUID();
        Transaction existing = Transaction.create(
                userId,
                "Conta de luz",
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150),
                "BRL",
                "CASA",
                TransactionType.WITHDRAW
        );

        when(repo.findById(existing.getId()))
                .thenReturn(Optional.of(existing));

        Transaction result = useCase.execute(existing.getId(), userId);

        assertEquals(existing.getId(), result.getId());
        verify(repo, times(1)).findById(existing.getId());
    }

    @Test
    void should_throw_exception_when_transaction_not_found() {

        TransactionRepository repo = mock(TransactionRepository.class);
        GetTransactionByIdUseCase useCase = new GetTransactionByIdUseCase(repo);

        UUID transactionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.findById(transactionId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(transactionId, userId)
        );

        verify(repo, times(1)).findById(transactionId);
    }

    @Test
    void should_throw_security_exception_when_user_is_not_owner() {

        TransactionRepository repo = mock(TransactionRepository.class);
        GetTransactionByIdUseCase useCase = new GetTransactionByIdUseCase(repo);

        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();

        Transaction existing = Transaction.create(
                ownerId,
                "Salário",
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(3000),
                "BRL",
                "RENDA",
                TransactionType.DEPOSIT
        );

        when(repo.findById(existing.getId()))
                .thenReturn(Optional.of(existing));

        assertThrows(
                SecurityException.class,
                () -> useCase.execute(existing.getId(), anotherUserId)
        );

        verify(repo, times(1)).findById(existing.getId());
    }

    @Test
    void should_throw_exception_when_user_id_is_null() {

        TransactionRepository repo = mock(TransactionRepository.class);
        GetTransactionByIdUseCase useCase = new GetTransactionByIdUseCase(repo);

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(UUID.randomUUID(), null)
        );

        verify(repo, never()).findById(org.mockito.ArgumentMatchers.any(UUID.class));
    }
}
