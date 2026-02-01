package com.finance.analytics_service.application.service;


import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.domain.repository.ExpenseRepository;
import com.finance.analytics_service.domain.usecase.GenerateReportUseCase;
import com.finance.analytics_service.domain.usecase.ProcessTransactionUseCase;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import com.finance.analytics_service.infrastructure.report.ExcelReportGenerator;
import com.finance.analytics_service.infrastructure.report.PdfReportGenerator;
import org.springframework.stereotype.Service;

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
        // já está persistido pelo consumer
        System.out.println("Despesa processada: " + expense.getDescription());
    }

    public ExpenseSummary getSummary(Long userId) {
        return expenseRepository.getSummaryByUser(userId);
    }

    @Override
    public byte[] generateExcel(Long userId) {
        return excelReportGenerator.generate(userId);
    }

    @Override
    public byte[] generatePdf(Long userId) {
        return pdfReportGenerator.generate(userId);
    }
}
