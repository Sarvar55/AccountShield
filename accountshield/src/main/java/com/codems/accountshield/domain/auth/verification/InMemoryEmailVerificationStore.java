package com.codems.accountshield.domain.auth.verification;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class InMemoryEmailVerificationStore {

    private static final int CODE_BOUND = 1_000_000;
    private static final Duration CODE_TTL = Duration.ofMinutes(10);

    private final Cache cache;
    private final SecureRandom secureRandom = new SecureRandom();

    public InMemoryEmailVerificationStore(CacheManager cacheManager) {
        Cache resolvedCache = cacheManager.getCache(com.codems.accountshield.common.constants.ApplicationConstants.EMAIL_VERIFICATION_CACHE_NAME);
        if (resolvedCache == null) {
            throw new IllegalStateException("Email verification cache is not available");
        }
        this.cache = resolvedCache;
    }

    public String createCode(String email) {
        String code = String.format("%06d", secureRandom.nextInt(CODE_BOUND));
        cache.put(code, new VerificationCodeEntry(email, LocalDateTime.now().plus(CODE_TTL)));
        return code;
    }

    public Optional<String> consumeEmailByCode(String code) {
        VerificationCodeEntry entry = cache.get(code, VerificationCodeEntry.class);
        if (entry == null) {
            return Optional.empty();
        }

        if (entry.isExpired()) {
            remove(code);
            return Optional.empty();
        }
        remove(code);
        return Optional.of(entry.email());
    }

    public void remove(String code) {
        cache.evict(code);
    }


    private record VerificationCodeEntry(String email, LocalDateTime expiresAt) {
        private boolean isExpired() {
            return expiresAt.isBefore(LocalDateTime.now());
        }
    }
}
