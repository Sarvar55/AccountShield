package com.codems.accountshield.domain.auth.dto;

import com.codems.accountshield.domain.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponse user
) {
}
