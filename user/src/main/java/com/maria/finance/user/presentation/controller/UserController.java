package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    // 🔒 ADMIN: lista todos | USER: lista só ele mesmo
    @GetMapping
    public List<UserResponseDTO> list(@AuthenticationPrincipal User user) {
        return service.list(user)
                .stream()
                .map(UserResponseDTO::fromDomain)
                .toList();
    }

    // 🔒 ADMIN: pode buscar qualquer ID | USER: só o próprio ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        User found = service.findById(id, user);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(found));
    }

    // 🔒 ADMIN: pode deletar | USER: não pode
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        service.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    // 🔒 ADMIN: atualiza qualquer usuário
    // 🔒 USER: só pode atualizar ele mesmo
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @RequestBody UserRequestDTO updatedUser,
            @AuthenticationPrincipal User requester) {

        User updated = service.update(id, updatedUser.toDomain(), requester);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(updated));
    }

}
