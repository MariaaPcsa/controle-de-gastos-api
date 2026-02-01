package com.finance.analytics_service.infrastructure.report;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;


@Component
public class ExcelReportGenerator {

    private final ExpenseRepositoryJpa repository;

    public ExcelReportGenerator(ExpenseRepositoryJpa repository) {
        this.repository = repository;
    }

    public byte[] generate(Long userId) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            var sheet = workbook.createSheet("Relatório");
            var row = sheet.createRow(0);
            row.createCell(0).setCellValue("Relatório do Usuário " + userId);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }
}


