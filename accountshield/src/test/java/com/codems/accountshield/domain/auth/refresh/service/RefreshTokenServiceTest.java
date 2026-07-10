package com.codems.accountshield.domain.auth.refresh.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codems.accountshield.common.security.service.JwtService;
import com.codems.accountshield.domain.auth.refresh.dto.TokenPair;
import com.codems.accountshield.domain.auth.refresh.entity.RefreshToken;
import com.codems.accountshield.domain.auth.refresh.repository.RefreshTokenRepository;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void rotateAcceptsBearerPrefixedRefreshToken() {
        RefreshTokenService service = new RefreshTokenService(jwtService, refreshTokenRepository, userRepository);

        User user = RefreshTokenTestMother.user();
        RefreshToken existing = RefreshTokenTestMother.existingRefreshToken(user);

        when(jwtService.extractUsername(RefreshTokenTestMother.RAW_REFRESH_TOKEN))
                .thenReturn(RefreshTokenTestMother.EMAIL);
        when(userRepository.findByEmail(RefreshTokenTestMother.EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.isRefreshTokenValid(RefreshTokenTestMother.RAW_REFRESH_TOKEN, user)).thenReturn(true);
        when(refreshTokenRepository.findByTokenHash(RefreshTokenTestMother.hash(RefreshTokenTestMother.RAW_REFRESH_TOKEN)))
                .thenReturn(Optional.of(existing));
        when(jwtService.generateRefreshToken(eq(user), any(), eq(existing.getFamilyId())))
                .thenReturn(RefreshTokenTestMother.NEW_REFRESH_TOKEN);
        when(jwtService.generateAccessToken(user)).thenReturn(RefreshTokenTestMother.NEW_ACCESS_TOKEN);
        when(jwtService.refreshExpirationMillis()).thenReturn(60_000L);

        TokenPair tokenPair = service.rotate(RefreshTokenTestMother.BEARER_REFRESH_TOKEN);

        assertThat(tokenPair.accessToken()).isEqualTo(RefreshTokenTestMother.NEW_ACCESS_TOKEN);
        assertThat(tokenPair.refreshToken()).isEqualTo(RefreshTokenTestMother.NEW_REFRESH_TOKEN);
        verify(jwtService).extractUsername(RefreshTokenTestMother.RAW_REFRESH_TOKEN);
        verify(jwtService).isRefreshTokenValid(RefreshTokenTestMother.RAW_REFRESH_TOKEN, user);
        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(refreshTokenRepository).findByTokenHash(hashCaptor.capture());
        assertThat(hashCaptor.getValue()).isEqualTo(RefreshTokenTestMother.hash(RefreshTokenTestMother.RAW_REFRESH_TOKEN));
    }
}
