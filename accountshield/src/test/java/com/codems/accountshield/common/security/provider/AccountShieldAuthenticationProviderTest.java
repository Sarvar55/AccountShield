package com.codems.accountshield.common.security.provider;

import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;
import com.codems.accountshield.domain.user.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AccountShieldAuthenticationProviderTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private AccountShieldAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        loginAttemptService = mock(LoginAttemptService.class);
        authenticationProvider = new AccountShieldAuthenticationProvider(
                userRepository,
                passwordEncoder,
                loginAttemptService,
                null
        );
    }

    @Test
    void authenticateRejectsUnverifiedEmail() {
        User user = AccountShieldAuthenticationProviderTestMother.unverifiedUser();

        when(userRepository.findByEmail(AccountShieldAuthenticationProviderTestMother.EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authenticationProvider.authenticate(
                AccountShieldAuthenticationProviderTestMother.authenticationToken()
        ))
                .isInstanceOf(DisabledException.class)
                .hasMessage("Email address is not verified. Please verify your email before logging in");

        verify(loginAttemptService).unlockIfExpired(user);
        verifyNoInteractions(passwordEncoder);
    }
}
