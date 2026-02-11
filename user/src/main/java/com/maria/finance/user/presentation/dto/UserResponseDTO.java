package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;


public record UserResponseDTO(
        Long id,
        String name,
        String email

) {
    public static UserResponseDTO fromDomain(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()

        );
    }
}

