package com.codems.accountshield.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.codems.accountshield.domain.user.entity.Role;

public record UserResponse(
        UUID id,
        String name,
        String bio,
        String email,
        Role role,
        boolean emailVerified,
        boolean accountLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
