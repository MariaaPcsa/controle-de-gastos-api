package com.finance.analytics_service.infrastructure;

import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class ExcelReportGenerator {

    private final ExpenseRepositoryJpa repository;

    public ExcelReportGenerator(ExpenseRepositoryJpa repository) {
        this.repository = repository;
    }

    public byte[] generate(Long userId) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Dashboard");

            // =========================
            // 🎨 TÍTULO MODERNO
            // =========================
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DASHBOARD FINANCEIRO");
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));

            // =========================
            // 💰 FORMATO MOEDA
            // =========================
            DataFormat format = workbook.createDataFormat();

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            // =========================
            // 📊 KPIs
            // =========================
            Font kpiFont = workbook.createFont();
            kpiFont.setBold(true);
            kpiFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle kpiStyle = workbook.createCellStyle();
            kpiStyle.setFont(kpiFont);
            kpiStyle.setAlignment(HorizontalAlignment.CENTER);
            kpiStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            kpiStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row kpiHeader = sheet.createRow(2);

            String[] kpis = {"Total Gasto", "Média", "Quantidade"};

            for (int i = 0; i < kpis.length; i++) {
                Cell cell = kpiHeader.createCell(i);
                cell.setCellValue(kpis[i]);
                cell.setCellStyle(kpiStyle);
            }

            // 🔥 dados reais (depois trocar pelo banco)
            double total = 2500.00;
            double media = 250.00;
            int qtd = 10;

            Row kpiValues = sheet.createRow(3);

            Cell totalCell = kpiValues.createCell(0);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(moneyStyle);

            Cell mediaCell = kpiValues.createCell(1);
            mediaCell.setCellValue(media);
            mediaCell.setCellStyle(moneyStyle);

            kpiValues.createCell(2).setCellValue(qtd);

            // =========================
            // 📋 TABELA
            // =========================
            Row header = sheet.createRow(5);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] cols = {"Data", "Descrição", "Valor"};

            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // =========================
            // 🦓 ZEBRA MODERNA
            // =========================
            CellStyle even = workbook.createCellStyle();
            even.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            even.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle odd = workbook.createCellStyle();
            odd.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            odd.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIndex = 6;

            for (int i = 0; i < 10; i++) {

                Row row = sheet.createRow(rowIndex);

                CellStyle style = (rowIndex % 2 == 0) ? even : odd;

                Cell c1 = row.createCell(0);
                c1.setCellValue("2026-04-22");
                c1.setCellStyle(style);

                Cell c2 = row.createCell(1);
                c2.setCellValue("Despesa " + i);
                c2.setCellStyle(style);

                Cell c3 = row.createCell(2);
                c3.setCellValue(100 + i * 10);
                c3.setCellStyle(moneyStyle);

                rowIndex++;
            }

            // =========================
            // 📏 AUTO SIZE
            // =========================
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar dashboard Excel", e);
        }
    }
}