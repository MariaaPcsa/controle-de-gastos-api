package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.UserType;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        UserType type
) {}
