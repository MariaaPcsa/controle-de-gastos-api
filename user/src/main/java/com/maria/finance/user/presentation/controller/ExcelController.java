package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.infrastructure.excel.UserExcelImporter;
import com.maria.finance.user.infrastructure.excel.UserImportErrorExporter;
import com.maria.finance.user.infrastructure.excel.UserImportResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@SecurityRequirement(name = "bearerAuth")
public class ExcelController {

    private final UserExcelImporter importer;
    private final UserImportErrorExporter exporter;

    public ExcelController(UserExcelImporter importer,
            UserImportErrorExporter exporter) {
        this.importer = importer;
        this.exporter = exporter;
    }

    // 🚀 IMPORTAÇÃO
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserImportResult> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        UserImportResult result = importer.importUsers(file);

        return ResponseEntity.ok(result);
    }

    // 📥 DOWNLOAD ERROS (versão correta)
    @PostMapping(value = "/errors/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadErrors(
            @RequestBody UserImportResult result) {

        if (result == null || result.getErrors() == null || result.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        byte[] file = exporter.exportErrors(result.getErrors());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=erros-importacao.xlsx")
                .body(file);
    }
}