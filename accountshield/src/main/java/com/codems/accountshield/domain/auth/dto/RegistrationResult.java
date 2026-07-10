package com.codems.accountshield.domain.auth.dto;

import com.codems.accountshield.domain.user.dto.UserResponse;

public record RegistrationResult(
        UserResponse user,
        String message
) {
}
