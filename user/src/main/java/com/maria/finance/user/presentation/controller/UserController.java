package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Users", description = "Endpoints de usuários")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService service;
    private final JwtService jwt;

    public UserController(UserApplicationService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @Operation(summary = "Buscar usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @Parameter(name = "id", description = "ID do usuário", example = "1", required = true)
            @PathVariable("id") Long id,

            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        User found = service.findById(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    @Operation(summary = "Listar usuários")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> list(
            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        List<UserResponseDTO> response = service.list(requester)
                .stream()
                .map(UserResponseDTO::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(name = "id", description = "ID do usuário a ser removido", example = "10", required = true)
            @PathVariable("id") Long id,

            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @Parameter(name = "id", description = "ID do usuário a ser atualizado", example = "10", required = true)
            @PathVariable("id") Long id,

            @RequestBody UserRequestDTO dto,

            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        User data = new User();
        data.setName(dto.name());
        data.setEmail(dto.email());
        data.setPassword(dto.password());
        data.setType(dto.type());

        User updated = service.update(id, data, requester);

        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }

    @Operation(summary = "Atualizar role (ADMIN)")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(
            @Parameter(name = "id", description = "ID do usuário", example = "10", required = true)
            @PathVariable("id") Long id,

            @RequestBody Map<String, String> body,

            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        UserType newType = UserType.valueOf(body.get("type").toUpperCase());

        User updated = service.updateRole(id, newType, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }

    @Operation(summary = "Reativar usuário (somente ADMIN)")
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<UserResponseDTO> reactivate(
            @Parameter(name = "id", description = "ID do usuário a ser reativado", example = "10", required = true)
            @PathVariable("id") Long id,

            @Parameter(description = "Token JWT no formato: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        User reactivated = service.reactivate(id, requester);

        return ResponseEntity.ok(UserResponseDTO.fromDomain(reactivated));
    }

}
