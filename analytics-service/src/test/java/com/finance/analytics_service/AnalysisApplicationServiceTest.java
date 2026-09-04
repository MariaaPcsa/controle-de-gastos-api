package com.finance.analytics_service;

import com.finance.analytics_service.domain.repository.ExpenseRepository;
import com.finance.analytics_service.infrastructure.ExcelReportGenerator;
import com.finance.analytics_service.infrastructure.PdfReportGenerator;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AnalysisApplicationServiceTest {

    @Test
    void shouldPersistExpenseOnProcess() {
        ExpenseRepository repository = mock(ExpenseRepository.class);
        ExcelReportGenerator excel = mock(ExcelReportGenerator.class);
        PdfReportGenerator pdf = mock(PdfReportGenerator.class);

        AnalysisApplicationService service =
                new AnalysisApplicationService(repository, excel, pdf);

        ExpenseEntity expense = new ExpenseEntity();
        expense.setUserId(UUID.randomUUID());
        expense.setDescription("Internet");
        expense.setCategory("SERVICOS");
        expense.setAmount(new BigDecimal("89.90"));
        expense.setDate(LocalDate.now());

        service.process(expense);

        verify(repository, times(1)).save(expense);
    }

    @Test
    void shouldRejectNullExpenseOnProcess() {
        ExpenseRepository repository = mock(ExpenseRepository.class);
        ExcelReportGenerator excel = mock(ExcelReportGenerator.class);
        PdfReportGenerator pdf = mock(PdfReportGenerator.class);

        AnalysisApplicationService service =
                new AnalysisApplicationService(repository, excel, pdf);

        assertThrows(IllegalArgumentException.class, () -> service.process(null));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
