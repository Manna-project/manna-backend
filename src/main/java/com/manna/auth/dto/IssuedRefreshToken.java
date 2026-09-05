package com.manna.auth.dto;

import java.time.LocalDateTime;

public record IssuedRefreshToken(
    String refreshToken,
    LocalDateTime expiresAt
) {
}
