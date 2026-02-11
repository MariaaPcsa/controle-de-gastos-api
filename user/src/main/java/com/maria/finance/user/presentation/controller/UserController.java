package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    // 🔒 ADMIN: pode buscar qualquer ID | USER: só o próprio ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        User found = service.findById(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    @Operation(summary = "Listar usuários")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> list(
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        List<UserResponseDTO> response = service.list(requester)
                .stream()
                .map(UserResponseDTO::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete usuários")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuários")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto,
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

    @Operation(summary = "Atualizar usuários somente ADMIN")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        UserType newType = UserType.valueOf(body.get("type").toUpperCase());

        User updated = service.updateRole(id, newType, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }


}
