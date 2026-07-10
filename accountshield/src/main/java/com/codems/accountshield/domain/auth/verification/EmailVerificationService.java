package com.codems.accountshield.domain.auth.verification;

import com.codems.accountshield.domain.user.entity.User;
import java.util.Optional;

public interface EmailVerificationService {

    EmailVerificationStartResult startVerification(User user);

    Optional<String> consumeEmailByCode(String code);
}
