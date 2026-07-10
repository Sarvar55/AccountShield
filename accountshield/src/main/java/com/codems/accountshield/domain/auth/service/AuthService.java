package com.codems.accountshield.domain.auth.service;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.common.exceptions.types.EmailVerificationException;
import com.codems.accountshield.domain.auth.dto.AuthResponse;
import com.codems.accountshield.domain.auth.dto.EmailVerificationRequest;
import com.codems.accountshield.domain.auth.dto.LoginRequest;
import com.codems.accountshield.domain.auth.dto.RegistrationResult;
import com.codems.accountshield.domain.auth.dto.RegisterRequest;
import com.codems.accountshield.domain.auth.dto.RefreshRequest;
import com.codems.accountshield.domain.auth.verification.EmailVerificationService;
import com.codems.accountshield.domain.auth.verification.EmailVerificationStartResult;
import com.codems.accountshield.domain.auth.refresh.dto.TokenPair;
import com.codems.accountshield.domain.auth.refresh.service.RefreshTokenService;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;
    @Nullable
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            AuthenticationManager authenticationManager,
            EmailVerificationService emailVerificationService,
            RefreshTokenService refreshTokenService,
            @Nullable CompromisedPasswordChecker compromisedPasswordChecker
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.emailVerificationService = emailVerificationService;
        this.refreshTokenService = refreshTokenService;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    @Transactional
    public RegistrationResult register(RegisterRequest request) {
        final String email = request.email().trim();
        final String rawPassword = request.password();
        log.info("Register requested for {}", email);
        checkCompromisedPassword(rawPassword);

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        EmailVerificationStartResult verificationResult = emailVerificationService.startVerification(savedUser);
        log.info("Registration completed for {} with verification flow", email);
        return new RegistrationResult(
                userMapper.toResponse(savedUser),
                verificationResult != null ? verificationResult.message() : "Verification email sent"
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim();
        log.debug("Login requested for {}", email);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow();
        log.info("Login succeeded for {}", email);
        TokenPair tokenPair = refreshTokenService.issueTokens(user);

        return new AuthResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                ApplicationConstants.AUTH_HEADER_PREFIX.trim(),
                userMapper.toResponse(user)
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        TokenPair tokenPair = refreshTokenService.rotate(request.refreshToken());
        return new AuthResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                ApplicationConstants.AUTH_HEADER_PREFIX.trim(),
                userMapper.toResponse(tokenPair.user())
        );
    }

    @Transactional
    public UserResponse verifyEmail(EmailVerificationRequest request) {
        log.debug("Email verification requested with code {}", request.code());
        String email = emailVerificationService.consumeEmailByCode(request.code())
                .orElseThrow(EmailVerificationException::new);

        User user = userRepository.findByEmail(email)
                .orElseThrow(EmailVerificationException::new);

        if (user.isEmailVerified()) {
            throw new EmailVerificationException();
        }

        user.setEmailVerified(true);
        log.info("Email verified for {}", email);
        return userMapper.toResponse(userRepository.save(user));
    }

    private void checkCompromisedPassword(String rawPassword) {
        boolean isPasswordCompromised = compromisedPasswordChecker != null
                && compromisedPasswordChecker.check(rawPassword).isCompromised();

        if (isPasswordCompromised) {
            throw new CompromisedPasswordException("The provided password is compromised, please change your password");
        }
    }
}
