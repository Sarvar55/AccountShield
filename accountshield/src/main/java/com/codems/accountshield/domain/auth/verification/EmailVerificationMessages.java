package com.codems.accountshield.domain.auth.verification;

public final class EmailVerificationMessages {

    public static final String INVALID_CODE = "Invalid email verification code";
    public static final String CODE_EXPIRED = "Email verification code has expired";

    private EmailVerificationMessages() {
        throw new UnsupportedOperationException("EmailVerificationMessages cannot be instantiated");
    }
}
