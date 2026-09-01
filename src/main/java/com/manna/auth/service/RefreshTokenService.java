package com.manna.auth.service;

import com.manna.auth.dto.IssuedRefreshToken;
import com.manna.auth.entity.RefreshSession;
import com.manna.auth.entity.User;
import com.manna.auth.repository.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(14);
    private static final int SECRET_BYTE_LENGTH = 32;

    private final RefreshSessionRepository refreshSessionRepository;
    private final Clock clock;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public IssuedRefreshToken issue(User user) {
        LocalDateTime now = LocalDateTime.now(clock);

        String secret = generateSecret();
        String tokenHash = hash(secret);

        RefreshSession session = RefreshSession.issue(user, tokenHash, now, REFRESH_TOKEN_EXPIRATION);

        refreshSessionRepository.save(session);

        String refreshToken = composeToken(session.getSessionId(), secret);

        return new IssuedRefreshToken(refreshToken, session.getExpiresAt());
    }

    private String generateSecret() {
        byte[] randomBytes = new byte[SECRET_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hash(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private String composeToken(UUID sessionId, String secret) {
        return sessionId + "." + secret;
    }
}
