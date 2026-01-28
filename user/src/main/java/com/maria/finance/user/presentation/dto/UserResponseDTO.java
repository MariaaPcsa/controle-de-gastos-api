package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserType type
) {
    public static UserResponseDTO fromDomain(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getType()
        );
    }
}

