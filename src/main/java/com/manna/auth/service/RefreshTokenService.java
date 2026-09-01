package com.manna.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    private final RefreshSession
}
