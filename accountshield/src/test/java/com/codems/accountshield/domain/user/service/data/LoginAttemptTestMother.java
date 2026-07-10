package com.codems.accountshield.domain.user.service.data;

import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;

import java.time.LocalDateTime;

public final class LoginAttemptTestMother {

    static final String NAME = "Sara";
    static final String EMAIL = "sara@example.com";
    static final String PASSWORD_HASH = "hash";

    private LoginAttemptTestMother() {
    }

    public static User user() {
        return User.builder()
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD_HASH)
                .role(Role.USER)
                .build();
    }

    public static User lockedUser() {
        return User.builder()
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD_HASH)
                .role(Role.USER)
                .accountLocked(true)
                .failedLoginAttempts(5)
                .lockedUntil(LocalDateTime.now().plusHours(1))
                .build();
    }
}
