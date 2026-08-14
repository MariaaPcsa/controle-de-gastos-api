package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteTransactionUseCaseTest {

    @Test
    void should_delete_transaction_successfully() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        DeleteTransactionUseCase useCase =
                new DeleteTransactionUseCase(repo);

        UUID userId = UUID.randomUUID();

        Transaction existing =
                Transaction.restore(
                        UUID.randomUUID(),       // transactionId
                        userId,                  // userId
                        "Salário",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(1000),
                        "BRL",
                        "SALÁRIO",
                        TransactionType.DEPOSIT,
                        LocalDateTime.now()
                );

        UUID transactionId =
                existing.getId();

        when(repo.findById(transactionId))
                .thenReturn(Optional.of(existing));

        // =====================================================
        // ACT
        // =====================================================

        assertDoesNotThrow(() ->
                useCase.execute(
                        transactionId,
                        userId
                )
        );

        // =====================================================
        // ASSERT
        // =====================================================

        verify(repo, times(1))
                .findById(transactionId);

        verify(repo, times(1))
                .deleteById(transactionId);
    }

    @Test
    void should_throw_exception_if_transaction_not_found() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        DeleteTransactionUseCase useCase =
                new DeleteTransactionUseCase(repo);

        UUID transactionId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        when(repo.findById(transactionId))
                .thenReturn(Optional.empty());

        // =====================================================
        // ACT + ASSERT
        // =====================================================

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        transactionId,
                        userId
                )
        );

        verify(repo, times(1))
                .findById(transactionId);

        verify(repo, never())
                .deleteById(any(UUID.class));
    }

    @Test
    void should_throw_exception_if_user_does_not_own_transaction() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        DeleteTransactionUseCase useCase =
                new DeleteTransactionUseCase(repo);

        UUID transactionOwner =
                UUID.randomUUID();

        UUID authenticatedUser =
                UUID.randomUUID();

        Transaction existing =
                Transaction.restore(
                        UUID.randomUUID(),
                        transactionOwner,
                        "Salário",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(1000),
                        "BRL",
                        "SALÁRIO",
                        TransactionType.DEPOSIT,
                        LocalDateTime.now()
                );

        UUID transactionId =
                existing.getId();

        when(repo.findById(transactionId))
                .thenReturn(Optional.of(existing));

        // =====================================================
        // ACT + ASSERT
        // =====================================================

        assertThrows(
                SecurityException.class,
                () -> useCase.execute(
                        transactionId,
                        authenticatedUser
                )
        );

        verify(repo, times(1))
                .findById(transactionId);

        verify(repo, never())
                .deleteById(any(UUID.class));
    }

    @Test
    void should_throw_exception_if_user_id_is_null() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        DeleteTransactionUseCase useCase =
                new DeleteTransactionUseCase(repo);

        UUID transactionId =
                UUID.randomUUID();

        // =====================================================
        // ACT + ASSERT
        // =====================================================

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        transactionId,
                        null
                )
        );

        verify(repo, never())
                .findById(any(UUID.class));

        verify(repo, never())
                .deleteById(any(UUID.class));
    }
}