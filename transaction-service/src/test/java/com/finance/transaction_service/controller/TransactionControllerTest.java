package com.finance.transaction_service.controller;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionControllerTest {


    @Test
    void should_update_transaction() {
        TransactionRepository repo = mock(TransactionRepository.class);
        UpdateTransactionUseCase useCase = new UpdateTransactionUseCase(repo);

        Transaction oldT = new Transaction(1L, 1L, "SALARIO", BigDecimal.TEN, BigDecimal.TEN, "BRL", TransactionType.DEPOSIT, null);
        Transaction newT = new Transaction(1L, 1L, "COMIDA", BigDecimal.valueOf(50), BigDecimal.valueOf(50), "BRL", TransactionType.PURCHASE, null);

        when(repo.findById(1L)).thenReturn(Optional.of(oldT));
        when(repo.save(any())).thenReturn(newT);

        Transaction result = useCase.execute(
                1L,
                "COMIDA",
                BigDecimal.valueOf(50), // convertedAmount
                BigDecimal.valueOf(50), // originalAmount
                "BRL",
                TransactionType.PURCHASE
        );

        assertEquals(TransactionType.PURCHASE, result.getType());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
    }}

