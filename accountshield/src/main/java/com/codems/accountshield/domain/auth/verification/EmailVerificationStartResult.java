package com.codems.accountshield.domain.auth.verification;

public record EmailVerificationStartResult(
        String message,
        String verificationCode
) {
}
