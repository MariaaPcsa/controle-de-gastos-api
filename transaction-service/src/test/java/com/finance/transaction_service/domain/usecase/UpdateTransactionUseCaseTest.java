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

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        UpdateTransactionUseCase useCase =
                new UpdateTransactionUseCase(repo);

        UUID userId = UUID.randomUUID();

        // Transação existente
        Transaction existing =
                Transaction.create(
                        userId,
                        "Salário",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(1000),
                        "BRL",
                        "SALÁRIO",
                        TransactionType.DEPOSIT
                );

        UUID transactionId =
                existing.getId();

        when(repo.findById(transactionId))
                .thenReturn(Optional.of(existing));

        when(repo.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        // =====================================================
        // ACT
        // =====================================================

        Transaction result =
                useCase.execute(
                        transactionId,
                        "Salário Atualizado",
                        BigDecimal.valueOf(1200),
                        BigDecimal.valueOf(1200),
                        "SALÁRIO",
                        TransactionType.DEPOSIT,
                        userId
                );

        // =====================================================
        // ASSERT
        // =====================================================

        assertNotNull(result);

        assertEquals(
                transactionId,
                result.getId()
        );

        assertEquals(
                userId,
                result.getUserId()
        );

        assertEquals(
                BigDecimal.valueOf(1200),
                result.getAmount()
        );

        assertEquals(
                BigDecimal.valueOf(1200),
                result.getOriginalAmount()
        );

        assertEquals(
                "Salário Atualizado",
                result.getDescription()
        );

        assertEquals(
                "SALÁRIO",
                result.getCategory()
        );

        assertEquals(
                TransactionType.DEPOSIT,
                result.getType()
        );

        // =====================================================
        // VERIFY
        // =====================================================

        verify(repo, times(1))
                .findById(transactionId);

        verify(repo, times(1))
                .save(existing);
    }

    @Test
    void should_throw_exception_if_transaction_not_found() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        UpdateTransactionUseCase useCase =
                new UpdateTransactionUseCase(repo);

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
                () ->
                        useCase.execute(
                                transactionId,
                                "Nova Descrição",
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(100),
                                "MISC",
                                TransactionType.DEPOSIT,
                                userId
                        )
        );

        verify(
                repo,
                times(1)
        ).findById(transactionId);

        verify(
                repo,
                never()
        ).save(any());
    }

    @Test
    void should_throw_exception_when_user_is_not_owner() {

        // =====================================================
        // ARRANGE
        // =====================================================

        TransactionRepository repo =
                mock(TransactionRepository.class);

        UpdateTransactionUseCase useCase =
                new UpdateTransactionUseCase(repo);

        UUID ownerId =
                UUID.randomUUID();

        UUID anotherUserId =
                UUID.randomUUID();

        Transaction existing =
                Transaction.create(
                        ownerId,
                        "Salário",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(1000),
                        "BRL",
                        "SALÁRIO",
                        TransactionType.DEPOSIT
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
                () ->
                        useCase.execute(
                                transactionId,
                                "Salário Atualizado",
                                BigDecimal.valueOf(1200),
                                BigDecimal.valueOf(1200),
                                "SALÁRIO",
                                TransactionType.DEPOSIT,
                                anotherUserId
                        )
        );

        // Não deve salvar
        verify(
                repo,
                never()
        ).save(any());
    }
}