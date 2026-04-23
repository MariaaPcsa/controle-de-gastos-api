package com.finance.analytics_service.infrastructure.report;

import com.finance.analytics_service.infrastructure.PdfReportGenerator;
import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfReportGeneratorTest {

    @Test
    void shouldGeneratePdfSuccessfully() {

        ExpenseRepositoryJpa repository = mock(ExpenseRepositoryJpa.class);
        when(repository.findByUserId(1L)).thenReturn(List.of());

        PdfReportGenerator generator = new PdfReportGenerator(repository);

        byte[] result = generator.generate(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}