package com.finance.analytics_service.infrastructure.report;

import com.finance.analytics_service.infrastructure.PdfReportGenerator;
import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfReportGeneratorTest {

    @Test
    void shouldGeneratePdfSuccessfully() {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");

        ExpenseRepositoryJpa repository = mock(ExpenseRepositoryJpa.class);
        when(repository.findByUserId(userId)).thenReturn(List.of());

        PdfReportGenerator generator = new PdfReportGenerator(repository);

        byte[] result = generator.generate(userId);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}