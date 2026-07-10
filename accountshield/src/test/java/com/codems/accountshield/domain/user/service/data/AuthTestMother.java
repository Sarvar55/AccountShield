package com.codems.accountshield.domain.user.service.data;

import com.codems.accountshield.domain.auth.dto.EmailVerificationRequest;
import com.codems.accountshield.domain.auth.dto.LoginRequest;
import com.codems.accountshield.domain.auth.dto.RefreshRequest;
import com.codems.accountshield.domain.auth.dto.RegisterRequest;
import com.codems.accountshield.domain.auth.refresh.dto.TokenPair;
import com.codems.accountshield.domain.auth.verification.EmailVerificationStartResult;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class AuthTestMother {

    public static final String NAME = "Sara";
    public static final String REGISTER_EMAIL = "Sara@Example.COM";
    public static final String EMAIL = "sara@example.com";
    public static final String PASSWORD = "password123";
    public static final String WRONG_PASSWORD = "wrong-password";
    public static final String VERIFICATION_CODE = "123456";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String NEW_ACCESS_TOKEN = "new-access-token";
    public static final String NEW_REFRESH_TOKEN = "new-refresh-token";

    private AuthTestMother() {
    }

    public static RegisterRequest registerRequest() {
        return new RegisterRequest(NAME, REGISTER_EMAIL, PASSWORD);
    }

    public static LoginRequest loginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }

    public static LoginRequest invalidLoginRequest() {
        return new LoginRequest(EMAIL, WRONG_PASSWORD);
    }

    public static RefreshRequest refreshRequest() {
        return new RefreshRequest(REFRESH_TOKEN);
    }

    public static EmailVerificationRequest emailVerificationRequest() {
        return new EmailVerificationRequest(VERIFICATION_CODE);
    }

    public static User registeredUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(NAME)
                .email(REGISTER_EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.USER)
                .emailVerified(false)
                .build();
    }

    public static User loginUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(NAME)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.USER)
                .build();
    }

    public static User unverifiedUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(NAME)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.USER)
                .emailVerified(false)
                .build();
    }

    public static User verifiedUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(NAME)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.USER)
                .emailVerified(true)
                .build();
    }

    public static UserResponse registeredUserResponse() {
        return new UserResponse(null, NAME, REGISTER_EMAIL, Role.USER, false, false, null, null);
    }

    public static UserResponse loginUserResponse() {
        return new UserResponse(null, NAME, EMAIL, Role.USER, false, false, null, null);
    }

    public static UserResponse verifiedUserResponse() {
        return new UserResponse(null, NAME, EMAIL, Role.USER, true, false, null, null);
    }

    public static EmailVerificationStartResult verificationSent() {
        return new EmailVerificationStartResult("Verification email sent", null);
    }

    public static EmailVerificationStartResult verificationSentWithCode() {
        return new EmailVerificationStartResult("Verification email sent. Code: " + VERIFICATION_CODE, VERIFICATION_CODE);
    }

    public static TokenPair tokenPair(User user) {
        return new TokenPair(ACCESS_TOKEN, REFRESH_TOKEN, user);
    }

    public static TokenPair rotatedTokenPair(User user) {
        return new TokenPair(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN, user);
    }
}
