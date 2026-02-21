package com.finance.transaction_service.controller;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.presentation.controller.TransactionController;
import com.finance.transaction_service.presentation.dto.TransactionResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Test
    void should_list_transactions() {
        TransactionApplicationService service = mock(TransactionApplicationService.class);
        TransactionController controller = new TransactionController(service);

        Transaction t1 = Transaction.restore(
                java.util.UUID.randomUUID(),
                1L,
                "Salário",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                "BRL",
                "Renda",
                TransactionType.DEPOSIT,
                LocalDateTime.now().minusDays(1)
        );

        Transaction t2 = Transaction.restore(
                java.util.UUID.randomUUID(),
                1L,
                "Aluguel",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(500),
                "BRL",
                "Moradia",
                TransactionType.WITHDRAW,
                LocalDateTime.now()
        );

        when(service.list(1L)).thenReturn(List.of(t1, t2));

        List<TransactionResponseDTO> result = controller.list(1L).getBody();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> "Salário".equals(t.getDescription())));
        assertTrue(result.stream().anyMatch(t -> "Aluguel".equals(t.getDescription())));

        verify(service, times(1)).list(1L);
    }
}