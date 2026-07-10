package com.codems.accountshield.domain.auth.verification;

import com.codems.accountshield.domain.user.entity.User;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Profile("!dev")
@Slf4j
public class ProdEmailVerificationService implements EmailVerificationService {

    private final InMemoryEmailVerificationStore verificationStore;

    public ProdEmailVerificationService(
            InMemoryEmailVerificationStore verificationStore
    ) {
        this.verificationStore = verificationStore;
    }

    @Override
    public EmailVerificationStartResult startVerification(User user) {
        verificationStore.createCode(user.getEmail());
        log.info("Verification code generated for {} in production profile", user.getEmail());
        return new EmailVerificationStartResult(
                "Verification email sent",
                null
        );
    }

    @Override
    public Optional<String> consumeEmailByCode(String code) {
        return verificationStore.consumeEmailByCode(code);
    }
}
