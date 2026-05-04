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

    private User getRequester(UserDetailsAdapter userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return userDetails.getDomainUser();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        User found = service.findById(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    @GetMapping
    @Operation(summary = "Listar usuários")
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

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<UserResponseDTO> create(
            @RequestBody UserRequestDTO dto,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setType(dto.type());

        User created = service.create(user);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Atualizar role")
    public ResponseEntity<UserResponseDTO> updateRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        UserType type = UserType.valueOf(body.get("type").toUpperCase());

        User updated = service.updateRole(id, type, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar usuário")
    public ResponseEntity<UserResponseDTO> reactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsAdapter userDetails
    ) {
        User requester = getRequester(userDetails);

        User reactivated = service.reactivate(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(reactivated));
    }
}