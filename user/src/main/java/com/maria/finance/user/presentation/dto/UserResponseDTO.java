package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.User;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
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