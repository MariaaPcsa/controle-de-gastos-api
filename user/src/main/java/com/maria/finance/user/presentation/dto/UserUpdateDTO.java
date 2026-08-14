package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;
import jakarta.validation.constraints.Email;

public record UserUpdateDTO(

        String name,

        @Email(message = "Email inválido")
        String email

) {
    public User toDomain() {
        User user = new User();

        if (this.name != null) {
            user.setName(this.name);
        }

        if (this.email != null) {
            user.setEmail(this.email);
        }

        return user;
    }
}