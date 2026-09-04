package com.manna.auth.service;

import com.manna.auth.dto.IssuedRefreshToken;
import com.manna.auth.entity.LoginType;
import com.manna.auth.entity.RefreshSession;
import com.manna.auth.entity.User;
import com.manna.auth.repository.RefreshSessionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshSessionRepository refreshSessionRepository;

    private RefreshTokenService refreshTokenService;
    private Clock fixedClock;
    private User user;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(
            Instant.parse("2026-09-01T01:00:00Z"),
            ZoneId.of("Asia/Seoul")
        );

        refreshTokenService = new RefreshTokenService(refreshSessionRepository, fixedClock);

        LocalDateTime now = LocalDateTime.now(fixedClock);

        user = User.builder()
            .loginType(LoginType.GOOGLE)
            .providerId("google-provider-id")
            .email("test@example.com")
            .name("테스트 사용자")
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Test
    void Refresh_Token을_발급하고_해시를_저장한다() throws Exception {
        IssuedRefreshToken result = refreshTokenService.issue(user);

        ArgumentCaptor<RefreshSession> sessionCaptor = ArgumentCaptor.forClass(RefreshSession.class);

        Mockito.verify(refreshSessionRepository).save(sessionCaptor.capture());

        RefreshSession savedSession = sessionCaptor.getValue();

        String[] tokenParts = result.refreshToken().split("\\.", 2);

        assertThat(tokenParts).hasSize(2);

        UUID sessionId = UUID.fromString(tokenParts[0]);
        String rawSecret = tokenParts[1];

        assertThat(sessionId).isEqualTo(savedSession.getSessionId());
        assertThat(savedSession.getTokenHash()).isEqualTo(sha256(rawSecret)).isNotEqualTo(rawSecret);
        assertThat(savedSession.getUser()).isSameAs(user);
    }

    @Test
    void Refresh_Token은_발급_시점으로부터_14일_후에_만료된다() {
        IssuedRefreshToken result = refreshTokenService.issue(user);

        LocalDateTime expectedExpiration = LocalDateTime.now(fixedClock).plusDays(14);

        assertThat(result.expiresAt()).isEqualTo(expectedExpiration);
    }

    @Test
    void 유효한_Refresh_Token이면_새로운_Refresh_Token으로_갱신한다() throws Exception {
        LocalDateTime now = LocalDateTime.now(fixedClock);
        String oldSecret = "old-refresh-secret";

        RefreshSession session = RefreshSession.issue(user, sha256(oldSecret), now.minusDays(1), Duration.ofDays(14));

        LocalDateTime originalExpiresAt = session.getExpiresAt();
        String rawRefreshToken = session.getSessionId() + "." + oldSecret;

        Mockito.when(refreshSessionRepository.findBySessionIdForUpdate(session.getSessionId()))
            .thenReturn(Optional.of(session));

        RefreshTokenService.RotationResult result = refreshTokenService
            .rotate(rawRefreshToken)
            .orElseThrow();

        String[] newTokenParts = result.issuedRefreshToken().refreshToken().split("\\.", 2);

        assertThat(newTokenParts).hasSize(2);

        String newSecret = newTokenParts[1];

        assertThat(UUID.fromString(newTokenParts[0])).isEqualTo(session.getSessionId());
        assertThat(newSecret).isNotEqualTo(oldSecret);
        assertThat(session.getPreviousTokenHash()).isEqualTo(sha256(oldSecret));
        assertThat(session.getTokenHash()).isEqualTo(sha256(newSecret));
        assertThat(session.getRotatedAt()).isEqualTo(now);
        assertThat(session.getLastUsedAt()).isEqualTo(now);
        assertThat(session.getExpiresAt()).isEqualTo(originalExpiresAt);
        assertThat(result.user()).isSameAs(user);
        assertThat(result.issuedRefreshToken().expiresAt()).isEqualTo(originalExpiresAt);
    }

    @Test
    void 유효한_Refresh_Token이면_현재_세션을_폐기한다() throws Exception {
        LocalDateTime now = LocalDateTime.now(fixedClock);
        String secret = "logout-test-secret";

        RefreshSession session = RefreshSession.issue(user, sha256(secret), now.minusDays(1), Duration.ofDays(14));

        String refreshToken = session.getSessionId() + "." + secret;

        Mockito.when(refreshSessionRepository.findBySessionIdForUpdate(session.getSessionId()))
            .thenReturn(Optional.of(session));

        refreshTokenService.revoke(refreshToken);

        assertThat(session.isRevoked()).isTrue();
        assertThat(session.getRevokedAt()).isEqualTo(now);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "invalid-token",
        "not-a-uuid.secret"
    })
    void 로그아웃은_잘못된_Refresh_Token이어도_예외를_노출하지_않는다(String invalidRefreshToken) {
        assertThatCode(() -> refreshTokenService.revoke(invalidRefreshToken))
            .doesNotThrowAnyException();

        Mockito.verifyNoInteractions(refreshSessionRepository);
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(hash);
    }
}
