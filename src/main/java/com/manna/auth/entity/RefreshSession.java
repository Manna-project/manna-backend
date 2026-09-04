package com.manna.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "refresh_sessions",
    indexes = {
        @Index(
            name = "idx_refresh_sessions_user_id",
            columnList = "user_id"
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 64)
    private String tokenHash;

    @Column(length = 64)
    private String previousTokenHash; // 직전 토큰 재사용과 동시 요청을 판단하기 위한 값

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime lastUsedAt;

    private LocalDateTime rotatedAt;

    private LocalDateTime revokedAt;

    public static RefreshSession issue(User user, String tokenHash, LocalDateTime now, Duration expiration) {
        RefreshSession session = new RefreshSession();

        session.sessionId = UUID.randomUUID();
        session.user = user;
        session.tokenHash = tokenHash;
        session.createdAt = now;
        session.expiresAt = now.plus(expiration);
        session.lastUsedAt = now;

        return session;
    }

    public void rotate(String newTokenHash, LocalDateTime now) {
        previousTokenHash = tokenHash;
        tokenHash = newTokenHash;
        rotatedAt = now;
        lastUsedAt = now;
    }

    public void revoke(LocalDateTime now) {
        if (revokedAt == null) {
            revokedAt = now;
        }
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
