package com.maria.finance.user.presentation.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Operation(
            summary = "Upload de arquivo Excel",
            description = "Recebe um arquivo Excel e processa seu conteúdo",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Arquivo processado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro no envio do arquivo")
            }
    )
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nenhum arquivo enviado.");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<String> data = new ArrayList<>();

            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    rowData.append(cell.toString()).append(" | ");
                }
                data.add(rowData.toString());
            }

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}

