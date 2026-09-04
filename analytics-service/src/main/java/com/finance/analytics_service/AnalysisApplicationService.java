package com.finance.analytics_service;


import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.domain.repository.ExpenseRepository;
import com.finance.analytics_service.domain.usecase.GenerateReportUseCase;
import com.finance.analytics_service.domain.usecase.ProcessTransactionUseCase;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import com.finance.analytics_service.infrastructure.ExcelReportGenerator;
import com.finance.analytics_service.infrastructure.PdfReportGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnalysisApplicationService implements ProcessTransactionUseCase, GenerateReportUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExcelReportGenerator excelReportGenerator;
    private final PdfReportGenerator pdfReportGenerator;

    public AnalysisApplicationService(
            ExpenseRepository expenseRepository,
            ExcelReportGenerator excelReportGenerator,
            PdfReportGenerator pdfReportGenerator
    ) {
        this.expenseRepository = expenseRepository;
        this.excelReportGenerator = excelReportGenerator;
        this.pdfReportGenerator = pdfReportGenerator;
    }

    @Override
    public void process(ExpenseEntity expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Despesa não pode ser nula");
        }

        if (expense.getUserId() == null) {
            throw new IllegalArgumentException("UserId da despesa é obrigatório");
        }

        expenseRepository.save(expense);
    }

    public ExpenseSummary getSummary(UUID userId) {
        return expenseRepository.getSummaryByUser(userId);
    }

    @Override
    public byte[] generateExcel(UUID userId) {
        return excelReportGenerator.generate(userId);
    }

    @Override
    public byte[] generatePdf(UUID userId) {
        return pdfReportGenerator.generate(userId);
    }
}
