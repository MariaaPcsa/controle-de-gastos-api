package com.maria.finance.user.presentation.dto;

import com.maria.finance.user.domain.model.UserType;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleDTO(

        @NotNull(message = "Tipo é obrigatório")
        UserType type

) {}