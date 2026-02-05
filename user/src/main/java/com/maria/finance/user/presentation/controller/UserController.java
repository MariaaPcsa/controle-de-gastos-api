package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        User found = service.findById(id, requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    // 🔒 ADMIN: lista todos | USER: só ele mesmo
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    // 🔒 ADMIN: atualiza qualquer usuário
// 🔒 USER: só pode atualizar ele mesmo
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable("id") Long id,
            @RequestBody UserRequestDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        User updated = service.update(
                id,
                new User(id, dto.name(), dto.email(), dto.password(), dto.type()),
                requester
        );

        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }
}



