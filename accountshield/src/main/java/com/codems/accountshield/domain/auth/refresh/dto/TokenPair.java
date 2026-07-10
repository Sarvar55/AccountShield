package com.codems.accountshield.domain.auth.refresh.dto;

import com.codems.accountshield.domain.user.entity.User;

public record TokenPair(
        String accessToken,
        String refreshToken,
        User user
) {
}
