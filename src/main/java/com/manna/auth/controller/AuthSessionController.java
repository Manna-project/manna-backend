package com.manna.auth.controller;

import com.manna.auth.dto.IssuedRefreshToken;
import com.manna.auth.service.JwtTokenService;
import com.manna.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthSessionController {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;
    private final Clock clock;

    @Value("${COOKIE_SECURE}")
    private boolean cookieSecure;

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refresh(@CookieValue(name = "refresh_token", required = false) String rawRefreshToken, HttpServletResponse response) {
        if (rawRefreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 없습니다.");
        }

        RefreshTokenService.RotationResult result = refreshTokenService.rotate(rawRefreshToken)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."));

        String accessToken = jwtTokenService.createAccessToken(result.user());

        addTokenCookies(response, accessToken, result.issuedRefreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue(name = "refresh_token", required = false) String rawRefreshToken) {
        if (rawRefreshToken == null) {
            return;
        }

        refreshTokenService.revoke(rawRefreshToken);
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, IssuedRefreshToken issuedRefreshToken) {
        ResponseCookie accessCookie = ResponseCookie
            .from("access_token", accessToken)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofMinutes(15))
            .build();

        Duration remainingRefreshLifetime = Duration.between(LocalDateTime.now(clock), issuedRefreshToken.expiresAt());

        ResponseCookie refreshCookie = ResponseCookie
            .from("refresh_token", issuedRefreshToken.refreshToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path("/api/v1/auth")
            .maxAge(remainingRefreshLifetime)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
