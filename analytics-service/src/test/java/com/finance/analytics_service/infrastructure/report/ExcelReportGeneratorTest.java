package com.finance.analytics_service.infrastructure.report;

import com.finance.analytics_service.infrastructure.ExcelReportGenerator;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelReportGeneratorTest {

    @Test
    void shouldGenerateExcelSuccessfully() {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");

        ExpenseEntity expense = new ExpenseEntity();
        expense.setUserId(userId);
        expense.setDescription("Mercado");
        expense.setCategory("ALIMENTACAO");
        expense.setAmount(new BigDecimal("100.50"));
        expense.setDate(LocalDate.of(2026, 9, 4));

        ExpenseRepositoryJpa repository = mock(ExpenseRepositoryJpa.class);
        when(repository.findByUserId(userId)).thenReturn(List.of(expense));

        ExcelReportGenerator generator = new ExcelReportGenerator(repository);

        byte[] result = generator.generate(userId);

        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(repository, times(1)).findByUserId(userId);

        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(result))) {
            var sheet = workbook.getSheet("Dashboard");
            assertNotNull(sheet);
            assertEquals("Mercado", sheet.getRow(6).getCell(1).getStringCellValue());
            assertEquals("ALIMENTACAO", sheet.getRow(6).getCell(2).getStringCellValue());
            assertEquals(100.50, sheet.getRow(6).getCell(3).getNumericCellValue(), 0.001);
        } catch (Exception e) {
            fail("Não foi possível validar conteúdo do Excel: " + e.getMessage());
        }
    }
}