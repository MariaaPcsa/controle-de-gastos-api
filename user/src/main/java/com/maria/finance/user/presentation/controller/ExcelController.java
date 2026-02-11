package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Users", description = "Endpoints de usuários")
@RequestMapping("/api/excel")
@SecurityRequirement(name = "bearerAuth")
public class ExcelController {

    private final UserApplicationService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ExcelController(UserApplicationService userService,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/upload")
    @Operation(
            summary = "Importar usuários via Excel",
            description = "Importa usuários a partir de um Excel (.xlsx). Apenas ADMIN.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum arquivo enviado.");
        }

        User requester = jwtService.getUserFromHeader(authHeader);

        if (!requester.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas ADMIN pode importar usuários.");
        }

        List<String> report = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // pula header

                try {
                    String email = getCellValue(row.getCell(0)); // Email
                    String name = getCellValue(row.getCell(1));  // Nome
                    String rawPassword = getCellValue(row.getCell(2)); // Senha

                    if (email.isBlank() || name.isBlank() || rawPassword.isBlank()) {
                        report.add("⚠️ Linha " + (row.getRowNum() + 1) + ": campos vazios");
                        continue;
                    }

                    String hashedPassword = passwordEncoder.encode(rawPassword);

                    User user = new User(null, name, email, hashedPassword, UserType.USER);

                    userService.createOrUpdate(user, requester);

                    report.add("✅ Importado: " + email);

                } catch (DataIntegrityViolationException e) {
                    report.add("⚠️ Email já existe: " + getCellValue(row.getCell(0)));

                } catch (IllegalArgumentException e) {
                    report.add("❌ Linha " + (row.getRowNum() + 1) + ": dados inválidos");

                } catch (Exception e) {
                    report.add("❌ Linha " + (row.getRowNum() + 1) + ": erro inesperado - " + e.getMessage());
                }
            }

            return ResponseEntity.ok(report);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
