package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
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
            description = "Recebe um arquivo Excel (.xlsx) e importa os usuários no banco. Apenas ADMIN pode acessar.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuários importados com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro no envio do arquivo"),
                    @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode importar")
            }
    )
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nenhum arquivo enviado.");
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
                    String name = getCellValue(row.getCell(0));
                    String email = getCellValue(row.getCell(1));
                    String rawPassword = getCellValue(row.getCell(2));
                    String typeStr = getCellValue(row.getCell(3));

                    if (name.isBlank() || email.isBlank() || rawPassword.isBlank()) {
                        report.add("Linha " + row.getRowNum() + " ignorada (campos vazios)");
                        continue;
                    }

                    UserType type = UserType.valueOf(typeStr.toUpperCase());
                    String hashedPassword = passwordEncoder.encode(rawPassword);

                    User user = new User(null, name, email, hashedPassword, type);

                    userService.createOrUpdate(user, requester);

                    report.add("Sucesso: " + email);

                } catch (Exception e) {
                    report.add("Erro na linha " + row.getRowNum() + ": " + e.getMessage());
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
