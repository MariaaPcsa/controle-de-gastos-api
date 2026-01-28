package com.finance.analytics_service.infrastructure.report;

import com.finance.analytics_service.domain.model.ExpenseSummary;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelReportGenerator {

    public static byte[] generate(List<ExpenseSummary> data) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Resumo");

        int row = 0;
        for (ExpenseSummary e : data) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(e.getCategory());
            r.createCell(1).setCellValue(e.getTotal().doubleValue());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}

