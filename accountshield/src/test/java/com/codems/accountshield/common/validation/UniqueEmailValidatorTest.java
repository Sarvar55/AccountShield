package com.codems.accountshield.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.codems.accountshield.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UniqueEmailValidatorTest {

    @Mock
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsFalseWhenEmailAlreadyExistsForDifferentUser() {
        UniqueEmailValidator validator = new UniqueEmailValidator(userRepository);
        when(userRepository.existsByEmail("sara@example.com")).thenReturn(true);

        boolean result = validator.isValid("sara@example.com", null);

        assertThat(result).isFalse();
    }

    @Test
    void allowsSameEmailForAuthenticatedCurrentUser() {
        UniqueEmailValidator validator = new UniqueEmailValidator(userRepository);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("sara@example.com", "password", List.of())
        );

        boolean result = validator.isValid("sara@example.com", null);

        assertThat(result).isTrue();
    }
}
