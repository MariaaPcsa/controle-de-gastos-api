package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Users", description = "Endpoints de usuários")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    // =====================================================
    // USUÁRIO AUTENTICADO
    // =====================================================

    private User getRequester(
            String userId,
            String userEmail,
            String userRole) {

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        if (userRole == null || userRole.isBlank()) {
            throw new RuntimeException("Role não encontrada");
        }

        return new User(
                UUID.fromString(userId),
                userEmail,
                null,
                null,
                UserType.valueOf(userRole)
        );
    }

    // =====================================================
    // BUSCAR USUÁRIO
    // =====================================================

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        User found = service.findById(id, requester);

        return ResponseEntity.ok(
                UserResponseDTO.fromDomain(found)
        );
    }

    // =====================================================
    // LISTAR USUÁRIOS
    // =====================================================

    @GetMapping
    @Operation(summary = "Listar usuários")
    public ResponseEntity<List<UserResponseDTO>> list(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        List<UserResponseDTO> response = service.list(requester)
                .stream()
                .map(UserResponseDTO::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // CRIAR USUÁRIO
    // =====================================================

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<UserResponseDTO> create(
            @RequestBody UserRequestDTO dto,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        User user = new User();

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setType(dto.type());

        User created = service.create(user);

        return ResponseEntity.ok(
                UserResponseDTO.fromDomain(created)
        );
    }

    // =====================================================
    // ATUALIZAR USUÁRIO
    // =====================================================

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UserRequestDTO dto,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        User data = new User();

        data.setName(dto.name());
        data.setEmail(dto.email());
        data.setPassword(dto.password());
        data.setType(dto.type());

        User updated = service.update(
                id,
                data,
                requester
        );

        return ResponseEntity.ok(
                UserResponseDTO.fromDomain(updated)
        );
    }

    // =====================================================
    // DELETAR
    // =====================================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        service.delete(id, requester);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // ALTERAR ROLE
    // =====================================================

    @PatchMapping("/{id}/role")
    @Operation(summary = "Atualizar role")
    public ResponseEntity<UserResponseDTO> updateRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        UserType type = UserType.valueOf(
                body.get("type").toUpperCase()
        );

        User updated = service.updateRole(
                id,
                type,
                requester
        );

        return ResponseEntity.ok(
                UserResponseDTO.fromDomain(updated)
        );
    }

    // =====================================================
    // REATIVAR
    // =====================================================

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar usuário")
    public ResponseEntity<UserResponseDTO> reactivate(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        User requester = getRequester(
                userId,
                userEmail,
                userRole
        );

        User reactivated = service.reactivate(
                id,
                requester
        );

        return ResponseEntity.ok(
                UserResponseDTO.fromDomain(reactivated)
        );
    }
}