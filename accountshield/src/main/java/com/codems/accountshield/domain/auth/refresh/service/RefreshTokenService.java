package com.codems.accountshield.domain.auth.refresh.service;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.common.exceptions.types.InvalidRefreshTokenException;
import com.codems.accountshield.common.security.service.JwtService;
import com.codems.accountshield.domain.auth.refresh.dto.TokenPair;
import com.codems.accountshield.domain.auth.refresh.entity.RefreshToken;
import com.codems.accountshield.domain.auth.refresh.repository.RefreshTokenRepository;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenPair issueTokens(User user) {
        return issueTokens(user, UUID.randomUUID().toString());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public TokenPair rotate(String refreshToken) {
        String token = normalizeRefreshToken(refreshToken);

        User user = validateAndResolveUser(token);
        String tokenHash = hash(token);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (existing.isRevoked() || existing.isExpired()) {
            throw new InvalidRefreshTokenException();
        }

        TokenPair pair = issueTokens(user, existing.getFamilyId());
        existing.revoke(hash(pair.refreshToken()));
        refreshTokenRepository.save(existing);
        return pair;
    }

    private TokenPair issueTokens(User user, String familyId) {
        String jti = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefreshToken(user, jti, familyId);
        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken stored = RefreshToken.builder()
                .jti(jti)
                .tokenHash(hash(refreshToken))
                .familyId(familyId)
                .user(user)
                .expiresAt(LocalDateTime.now().plusNanos(jwtService.refreshExpirationMillis() * 1_000_000))
                .build();

        refreshTokenRepository.save(stored);
        log.debug("Issued refresh token for {}", user.getEmail());
        return new TokenPair(accessToken, refreshToken, user);
    }

    private User validateAndResolveUser(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(InvalidRefreshTokenException::new);

            if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
                throw new InvalidRefreshTokenException();
            }
            return user;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidRefreshTokenException();
        }
    }

    private String normalizeRefreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
            return refreshToken.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length());
        }
        return refreshToken;
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash refresh token", e);
        }
    }
}
