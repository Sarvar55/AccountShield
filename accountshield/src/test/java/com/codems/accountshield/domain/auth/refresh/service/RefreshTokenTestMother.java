package com.codems.accountshield.domain.auth.refresh.service;

import com.codems.accountshield.domain.auth.refresh.entity.RefreshToken;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

final class RefreshTokenTestMother {

    static final String EMAIL = "sara@example.com";
    static final String NAME = "Sara";
    static final String RAW_REFRESH_TOKEN = "refresh-token";
    static final String BEARER_REFRESH_TOKEN = "Bearer " + RAW_REFRESH_TOKEN;
    static final String NEW_REFRESH_TOKEN = "new-refresh-token";
    static final String NEW_ACCESS_TOKEN = "new-access-token";

    private RefreshTokenTestMother() {
    }

    static User user() {
        return User.builder()
                .email(EMAIL)
                .name(NAME)
                .role(Role.USER)
                .build();
    }

    static RefreshToken existingRefreshToken(User user) {
        return RefreshToken.builder()
                .jti(UUID.randomUUID().toString())
                .tokenHash(hash(RAW_REFRESH_TOKEN))
                .familyId(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
