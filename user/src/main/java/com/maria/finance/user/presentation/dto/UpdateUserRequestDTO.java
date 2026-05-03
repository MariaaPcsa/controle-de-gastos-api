package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import jakarta.validation.constraints.Email;

public record UpdateUserRequestDTO(

        String name,

        @Email(message = "Email inválido")
        String email,

        String password,

        UserType type

) {

    public User toDomain() {
        User user = new User();

        if (name != null) {
            user.setName(name);
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (password != null) {
            user.setPassword(password);
        }

        if (type != null) {
            user.setType(type);
        }

        return user;
    }
}