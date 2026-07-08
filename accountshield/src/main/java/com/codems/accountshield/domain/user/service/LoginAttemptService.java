package com.codems.accountshield.domain.user.service;

import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_HOURS = 1;

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLogin(String email) {
        String lookupEmail = email.trim();
        userRepository.findByEmail(lookupEmail).ifPresent(user -> {
            if (isCurrentlyLocked(user)) {
                log.debug("Skipping failed login count because user is already locked: {}", lookupEmail);
                return;
            }

            int failedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedAttempts);
            log.warn("Failed login recorded for {} ({}/{})", lookupEmail, failedAttempts, MAX_FAILED_ATTEMPTS);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockedUntil(LocalDateTime.now().plusHours(LOCK_DURATION_HOURS));
                log.warn("User locked for 1 hour after failed logins: {}", lookupEmail);
            }
        });
    }

    @Transactional
    public void resetFailedLogins(User user) {
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        log.info("Failed login counters reset for {}", user.getEmail());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean unlockIfExpired(User user) {
        if (user.getLockedUntil() == null || user.getLockedUntil().isAfter(LocalDateTime.now())) {
            return false;
        }

        resetFailedLogins(user);
        log.info("Unlocked expired account for {}", user.getEmail());
        return true;
    }

    public boolean isCurrentlyLocked(User user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }
}
