package com.codems.accountshield.domain.auth.verification;

import com.codems.accountshield.domain.user.entity.User;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Profile("dev")
@Slf4j
public class DevEmailVerificationService implements EmailVerificationService {

    private final InMemoryEmailVerificationStore verificationStore;

    public DevEmailVerificationService(
            InMemoryEmailVerificationStore verificationStore
    ) {
        this.verificationStore = verificationStore;
    }

    @Override
    public EmailVerificationStartResult startVerification(User user) {
        String code = verificationStore.createCode(user.getEmail());
        log.info("Dev verification code generated for {}: {}", user.getEmail(), code);
        return new EmailVerificationStartResult(
                "Verification email sent. Code: " + code,
                code
        );
    }

    @Override
    public Optional<String> consumeEmailByCode(String code) {
        return verificationStore.consumeEmailByCode(code);
    }
}
