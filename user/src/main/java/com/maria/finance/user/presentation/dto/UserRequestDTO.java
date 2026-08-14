package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank
        String name,
        @Email(message = "Email inválido")
        @NotBlank
        String email,
        @NotBlank
        String password,
        UserType type
) {
    public User toDomain() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setType(this.type);
        return user;
    }
}

