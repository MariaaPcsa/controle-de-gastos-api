package com.finance.analytics_service.infrastructure.report;

import com.finance.analytics_service.infrastructure.PdfReportGenerator;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfReportGeneratorTest {

    @Test
    void shouldGeneratePdfSuccessfully() {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");

        ExpenseEntity expense = new ExpenseEntity();
        expense.setUserId(userId);
        expense.setDescription("Aluguel");
        expense.setCategory("MORADIA");
        expense.setAmount(new BigDecimal("950.00"));
        expense.setDate(LocalDate.of(2026, 9, 4));

        ExpenseRepositoryJpa repository = mock(ExpenseRepositoryJpa.class);
        when(repository.findByUserId(userId)).thenReturn(List.of(expense));

        PdfReportGenerator generator = new PdfReportGenerator(repository);

        byte[] result = generator.generate(userId);

        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(repository, times(1)).findByUserId(userId);

        try {
            PdfReader reader = new PdfReader(result);
            String pageText = new PdfTextExtractor(reader).getTextFromPage(1);
            reader.close();

            assertTrue(pageText.contains("RELATÓRIO FINANCEIRO"));
            assertTrue(pageText.contains("Aluguel"));
            assertTrue(pageText.contains("MORADIA"));
        } catch (Exception e) {
            fail("Não foi possível validar conteúdo do PDF: " + e.getMessage());
        }
    }
}