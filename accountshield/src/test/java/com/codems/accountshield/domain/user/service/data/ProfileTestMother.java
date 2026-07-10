package com.codems.accountshield.domain.user.service.data;

import com.codems.accountshield.domain.user.dto.ProfileUpdateRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;

public final class ProfileTestMother {

    static final String OLD_NAME = "Old";
    public static final String NEW_NAME = "New";
    public static final String EMAIL = "old@example.com";
    static final String OLD_BIO = "Old bio";
    public static final String NEW_BIO = "New bio";
    static final String PASSWORD_HASH = "hash";

    private ProfileTestMother() {
    }

    public static User userWithProfile() {
        return User.builder()
                .name(OLD_NAME)
                .email(EMAIL)
                .password(PASSWORD_HASH)
                .role(Role.USER)
                .bio(OLD_BIO)
                .build();
    }

    public static ProfileUpdateRequest updateRequest() {
        return new ProfileUpdateRequest(NEW_NAME, NEW_BIO);
    }

    public static UserResponse updatedResponse() {
        return new UserResponse(null, NEW_NAME, NEW_BIO, EMAIL, Role.USER, false, false, null, null);
    }
}
