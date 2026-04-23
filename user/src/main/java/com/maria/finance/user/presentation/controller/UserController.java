package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.UserDetailsAdapter;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Users", description = "Endpoints de usuários")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    // 🔥 Método auxiliar (evita repetição)
    private User getRequester(UserDetailsAdapter userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return userDetails.getDomainUser();
    }

    @Operation(summary = "Buscar usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        User found = service.findById(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    @Operation(summary = "Listar usuários")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> list(
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        List<UserResponseDTO> response = service.list(requester)
                .stream()
                .map(UserResponseDTO::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

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
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        UserType newType = UserType.valueOf(body.get("type").toUpperCase());

        User updated = service.updateRole(id, newType, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }

    @Operation(summary = "Reativar usuário (somente ADMIN)")
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<UserResponseDTO> reactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        User reactivated = service.reactivate(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(reactivated));
    }
}