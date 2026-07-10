package com.codems.accountshield.domain.user.service;

import com.codems.accountshield.domain.auth.dto.AuthResponse;
import com.codems.accountshield.domain.auth.dto.RegistrationResult;
import com.codems.accountshield.domain.auth.refresh.service.RefreshTokenService;
import com.codems.accountshield.domain.auth.service.AuthService;
import com.codems.accountshield.domain.auth.verification.EmailVerificationService;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;
import com.codems.accountshield.domain.user.service.data.AuthTestMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                userMapper,
                authenticationManager,
                emailVerificationService,
                refreshTokenService,
                null
        );
    }

    @Test
    void registerHashesPasswordAndCreatesUserRole() {
        User saved = AuthTestMother.registeredUser(passwordEncoder);
        UserResponse response = AuthTestMother.registeredUserResponse();

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);
        when(emailVerificationService.startVerification(saved))
                .thenReturn(AuthTestMother.verificationSent());

        RegistrationResult result = authService.register(AuthTestMother.registerRequest());

        assertThat(result.user().email()).isEqualTo(AuthTestMother.REGISTER_EMAIL);
        assertThat(result.message()).isEqualTo("Verification email sent");
        verify(userRepository).save(any(User.class));
        verify(emailVerificationService).startVerification(saved);
    }

    @Test
    void registerReturnsCodeMessageInDevSimulation() {
        User saved = AuthTestMother.registeredUser(passwordEncoder);
        UserResponse response = AuthTestMother.registeredUserResponse();

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);
        when(emailVerificationService.startVerification(saved))
                .thenReturn(AuthTestMother.verificationSentWithCode());

        RegistrationResult result = authService.register(AuthTestMother.registerRequest());

        assertThat(result.user().emailVerified()).isFalse();
        assertThat(result.message()).contains(AuthTestMother.VERIFICATION_CODE);
        verify(emailVerificationService).startVerification(saved);
    }

    @Test
    void registerRejectsCompromisedPasswordBeforeSavingUser() {
        CompromisedPasswordChecker compromisedPasswordChecker = mock(CompromisedPasswordChecker.class);
        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                userMapper,
                authenticationManager,
                emailVerificationService,
                refreshTokenService,
                compromisedPasswordChecker
        );

        when(compromisedPasswordChecker.check(AuthTestMother.PASSWORD))
                .thenReturn(new CompromisedPasswordDecision(true));

        assertThatThrownBy(() -> authService.register(AuthTestMother.registerRequest()))
                .isInstanceOf(CompromisedPasswordException.class)
                .hasMessage("The provided password is compromised, please change your password");

        verify(compromisedPasswordChecker).check(AuthTestMother.PASSWORD);
        verifyNoInteractions(userRepository, userMapper, emailVerificationService);
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        User user = AuthTestMother.loginUser(passwordEncoder);
        UserResponse userResponse = AuthTestMother.loginUserResponse();

        when(userRepository.findByEmail(AuthTestMother.EMAIL)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenService.issueTokens(user))
                .thenReturn(AuthTestMother.tokenPair(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse response = authService.login(AuthTestMother.loginRequest());

        assertThat(response.accessToken()).isEqualTo(AuthTestMother.ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(AuthTestMother.REFRESH_TOKEN);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().email()).isEqualTo(AuthTestMother.EMAIL);
    }

    @Test
    void verifyEmailEnablesUser() {
        User user = AuthTestMother.unverifiedUser(passwordEncoder);
        UserResponse userResponse = AuthTestMother.verifiedUserResponse();

        when(emailVerificationService.consumeEmailByCode(AuthTestMother.VERIFICATION_CODE))
                .thenReturn(Optional.of(AuthTestMother.EMAIL));
        when(userRepository.findByEmail(AuthTestMother.EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse response = authService.verifyEmail(AuthTestMother.emailVerificationRequest());

        assertThat(response.emailVerified()).isTrue();
    }

    @Test
    void refreshRotatesRefreshToken() {
        User user = AuthTestMother.verifiedUser(passwordEncoder);
        UserResponse userResponse = AuthTestMother.verifiedUserResponse();

        when(refreshTokenService.rotate(AuthTestMother.REFRESH_TOKEN))
                .thenReturn(AuthTestMother.rotatedTokenPair(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse response = authService.refresh(AuthTestMother.refreshRequest());

        assertThat(response.accessToken()).isEqualTo(AuthTestMother.NEW_ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(AuthTestMother.NEW_REFRESH_TOKEN);
        assertThat(response.user().email()).isEqualTo(AuthTestMother.EMAIL);
    }

    @Test
    void loginRejectsInvalidPassword() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThatThrownBy(() -> authService.login(AuthTestMother.invalidLoginRequest()))
                .isInstanceOf(BadCredentialsException.class);
    }
}
