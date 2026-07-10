package com.codems.accountshield.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserStatusRequest(
        @NotNull Boolean accountLocked,
        @NotNull Boolean emailVerified
) {
}
