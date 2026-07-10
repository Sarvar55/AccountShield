package com.codems.accountshield.common.security.provider;

import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

final class AccountShieldAuthenticationProviderTestMother {

    static final String EMAIL = "sara@example.com";
    static final String PASSWORD = "password";
    static final String ENCODED_PASSWORD = "encoded-password";

    private AccountShieldAuthenticationProviderTestMother() {
    }

    static User unverifiedUser() {
        return User.builder()
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .role(Role.USER)
                .emailVerified(false)
                .build();
    }

    static UsernamePasswordAuthenticationToken authenticationToken() {
        return new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD);
    }
}
