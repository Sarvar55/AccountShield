package com.codems.accountshield.domain.user.dto;

import com.codems.accountshield.domain.user.entity.Role;

import jakarta.validation.constraints.NotNull;

public record AdminUserRoleRequest(
        @NotNull Role role
) {
}
