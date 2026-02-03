package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;

public record UserUpdateDTO(
        String name,
        String email
) {
    // Converte o DTO para um objeto User parcial (apenas com os campos que podem ser alterados)
    public User toDomain() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        return user;
    }
}

