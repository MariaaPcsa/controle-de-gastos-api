package com.maria.finance.user.infrastructure.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class UserImportErrorExporter {

    public byte[] exportErrors(List<UserImportError> errors) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Erros Importação");

            // 🔥 HEADER
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Linha");
            header.createCell(1).setCellValue("Tipo");
            header.createCell(2).setCellValue("Mensagem");
            header.createCell(3).setCellValue("Email");

            // 🎨 Estilo header (opcional)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (Cell cell : header) {
                cell.setCellStyle(headerStyle);
            }

            // 📄 DADOS
            int rowIdx = 1;
            for (UserImportError error : errors) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(error.getLinha());
                row.createCell(1).setCellValue(error.getTipo());
                row.createCell(2).setCellValue(error.getMensagem());
                row.createCell(3).setCellValue(
                        error.getEmail() != null ? error.getEmail() : ""
                );
            }

            // 📏 Auto-size colunas
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel de erros", e);
        }
    }
}