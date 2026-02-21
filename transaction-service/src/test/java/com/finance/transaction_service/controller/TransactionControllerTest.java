package com.finance.transaction_service.controller;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.presentation.controller.TransactionController;
import com.finance.transaction_service.presentation.dto.FilterTransactionDTO;
import com.finance.transaction_service.presentation.dto.PagedResponseDTO;
import com.finance.transaction_service.presentation.dto.TransactionResponseDTO;
import com.finance.transaction_service.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class TransactionControllerTest {

    private TransactionApplicationService service;
    private TransactionController controller;

    @BeforeEach
    void setup() {
        service = mock(TransactionApplicationService.class);
        controller = new TransactionController(service);
    }

    @Test
    void should_list_transactions() {

        // 🔐 Mock do usuário autenticado
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        Transaction t1 = Transaction.restore(
                UUID.randomUUID(),
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
                UUID.randomUUID(),
                1L,
                "Aluguel",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(500),
                "BRL",
                "Moradia",
                TransactionType.WITHDRAW,
                LocalDateTime.now()
        );

        // Mock do novo método com FilterTransactionDTO
        when(service.list(eq(1L), any(FilterTransactionDTO.class))).thenReturn(List.of(t1, t2));

        ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> response =
                controller.list(userDetails, null, null, null, null, 0, 10, "createdAt", "DESC");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        PagedResponseDTO<TransactionResponseDTO> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertTrue(result.getContent().stream()
                .anyMatch(t -> "Salário".equals(t.getDescription())));
        assertTrue(result.getContent().stream()
                .anyMatch(t -> "Aluguel".equals(t.getDescription())));

        verify(service, times(1)).list(eq(1L), any(FilterTransactionDTO.class));
    }
}