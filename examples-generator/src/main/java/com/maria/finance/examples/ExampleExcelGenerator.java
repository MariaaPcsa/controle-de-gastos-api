package com.maria.finance.examples;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ExampleExcelGenerator {

    public static void main(String[] args) throws Exception {
        var workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("transactions");

        // header style
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        ((XSSFCellStyle) headerStyle).setFont(font);

        // Header
        Row header = sheet.createRow(0);
        String[] columns = new String[]{"userId","occurredAt","type","amount","currency","category","description","targetUserId"};
        for (int i = 0; i < columns.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(columns[i]);
            c.setCellStyle(headerStyle);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Example rows
        Object[][] data = new Object[][]{
                {"00000000-0000-0000-0000-000000000001", dtf.format(LocalDateTime.now().minusDays(7)), "DEPOSIT", new BigDecimal("1500.00"), "BRL", "SALARY", "Salário mensal", null},
                {"00000000-0000-0000-0000-000000000001", dtf.format(LocalDateTime.now().minusDays(5)), "PURCHASE", new BigDecimal("200.50"), "BRL", "GROCERIES", "Supermercado", null},
                {"00000000-0000-0000-0000-000000000002", dtf.format(LocalDateTime.now().minusDays(2)), "TRANSFER", new BigDecimal("500.00"), "USD", "RENT", "Transferência aluguel", "00000000-0000-0000-0000-000000000003"}
        };

        int rowIdx = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowIdx++);
            for (int col = 0; col < rowData.length; col++) {
                Cell cell = row.createCell(col);
                Object v = rowData[col];
                if (v == null) {
                    cell.setBlank();
                } else if (v instanceof String) {
                    cell.setCellValue((String) v);
                } else if (v instanceof BigDecimal) {
                    cell.setCellValue(((BigDecimal) v).doubleValue());
                } else {
                    cell.setCellValue(String.valueOf(v));
                }
            }
        }

        // Autosize
        for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

        // Output file
        File outDir = new File(System.getProperty("user.dir"), "../examples");
        if (!outDir.exists()) outDir.mkdirs();
        File outFile = new File(outDir, "transactions_upload_example.xlsx");
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("Generated: " + outFile.getAbsolutePath());
    }
}

