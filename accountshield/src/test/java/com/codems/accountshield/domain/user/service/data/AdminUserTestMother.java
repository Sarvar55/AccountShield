package com.codems.accountshield.domain.user.service.data;

import com.codems.accountshield.domain.user.dto.AdminUserRoleRequest;
import com.codems.accountshield.domain.user.dto.AdminUserStatusRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;

import java.util.UUID;

public final class AdminUserTestMother {

    static final String NAME = "Sara";
    static final String EMAIL = "sara@example.com";
    static final String PASSWORD_HASH = "hash";

    private AdminUserTestMother() {
    }

   public static User regularUser() {
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
                .failedLoginAttempts(3)
                .build();
    }

    public  static AdminUserRoleRequest adminRoleRequest() {
        return new AdminUserRoleRequest(Role.ADMIN);
    }

    public  static AdminUserStatusRequest unlockedStatusRequest() {
        return new AdminUserStatusRequest(false,false);
    }

    public static UserResponse adminResponse(UUID id) {
        return new UserResponse(id, NAME, null, EMAIL, Role.ADMIN, true, false, null, null);
    }

    public static UserResponse unlockedUserResponse(UUID id) {
        return new UserResponse(id, NAME, null, EMAIL, Role.USER, false, false, null, null);
    }
}
