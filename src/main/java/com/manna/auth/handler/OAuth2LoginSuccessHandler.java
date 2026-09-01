package com.manna.auth.handler;

import com.manna.auth.dto.AuthUserInfo;
import com.manna.auth.dto.IssuedRefreshToken;
import com.manna.auth.entity.User;
import com.manna.auth.service.AuthService;
import com.manna.auth.service.JwtTokenService;
import com.manna.auth.service.OAuth2UserInfoService;
import com.manna.auth.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final OAuth2UserInfoService oauth2UserInfoService;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Value("${FRONTEND_BASE_URL}")
    private String frontendBaseUrl;

    @Value("${COOKIE_SECURE}")
    private boolean cookieSecure;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        AuthUserInfo userInfo = oauth2UserInfoService.extract(registrationId, oauth2User.getAttributes());

        User loginUser = authService.login(userInfo);

        String accessToken = jwtTokenService.createAccessToken(loginUser);
        IssuedRefreshToken issuedRefreshToken = refreshTokenService.issue(loginUser);

        addTokenCookies(response, accessToken, issuedRefreshToken.refreshToken());

        response.sendRedirect(frontendBaseUrl + "/oauth/callback");
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie
            .from("access_token", accessToken)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofMinutes(15))
            .build();

        ResponseCookie refreshCookie = ResponseCookie
            .from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path("/api/v1/auth")
            .maxAge(Duration.ofDays(14))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}