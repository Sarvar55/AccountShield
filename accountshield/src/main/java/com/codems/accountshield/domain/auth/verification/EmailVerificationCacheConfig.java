package com.codems.accountshield.domain.auth.verification;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codems.accountshield.common.constants.ApplicationConstants;

@Configuration
public class EmailVerificationCacheConfig {

    @Bean
    public CacheManager emailVerificationCacheManager() {
        return new ConcurrentMapCacheManager(ApplicationConstants.EMAIL_VERIFICATION_CACHE_NAME);
    }
}
