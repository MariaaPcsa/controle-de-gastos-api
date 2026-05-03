package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.excel.*;
import com.maria.finance.user.infrastructure.security.UserDetailsAdapter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private User getRequester(UserDetailsAdapter userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return userDetails.getDomainUser();
    }

    // 🚀 IMPORTAÇÃO
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Arquivo não enviado ou vazio");
        }

        User requester = getRequester(userDetails);

        if (!requester.isAdmin()) {
            return ResponseEntity.status(403)
                    .body("Apenas ADMIN pode importar usuários");
        }

        UserImportResult result = importer.importUsers(file);

        return ResponseEntity.ok(result);
    }

    // 📥 DOWNLOAD DOS ERROS
    @PostMapping("/errors/download")
    public ResponseEntity<?> downloadErrors(
            @RequestBody UserImportResult result,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {

        User requester = getRequester(userDetails);

        if (!requester.isAdmin()) {
            return ResponseEntity.status(403)
                    .body("Apenas ADMIN pode baixar erros");
        }

        if (result == null || result.getErrors() == null || result.getErrors().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Não há erros para exportar");
        }

        byte[] file = exporter.exportErrors(result.getErrors());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=erros-importacao.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}