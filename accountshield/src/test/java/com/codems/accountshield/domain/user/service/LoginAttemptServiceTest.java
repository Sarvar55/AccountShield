package com.codems.accountshield.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.codems.accountshield.domain.user.service.data.LoginAttemptTestMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    @Test
    void recordFailedLoginLocksUserForOneHourAfterFiveFailures() {
        User user = LoginAttemptTestMother.user();

        when(userRepository.findByEmail(LoginAttemptTestMother.EMAIL)).thenReturn(Optional.of(user));

        for (int i = 0; i < 5; i++) {
            loginAttemptService.recordFailedLogin(LoginAttemptTestMother.EMAIL);
        }

        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(user.isAccountLocked()).isTrue();
        assertThat(user.getLockedUntil()).isNotNull();
        assertThat(user.getLockedUntil()).isAfter(java.time.LocalDateTime.now().plusMinutes(59));
    }

    @Test
    void resetFailedLoginsUnlocksUser() {
        User user = LoginAttemptTestMother.lockedUser();

        loginAttemptService.resetFailedLogins(user);

        assertThat(user.getFailedLoginAttempts()).isZero();
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getLockedUntil()).isNull();
    }
}
