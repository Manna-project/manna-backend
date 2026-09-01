package com.manna.auth.controller;

import com.manna.auth.service.JwtTokenService;
import com.manna.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AuthSessionControllerTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtTokenService jwtTokenService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
            Instant.parse("2026-09-01T01:00:00Z"),
            ZoneId.of("Asia/Seoul")
        );

        AuthSessionController controller =
            new AuthSessionController(
                refreshTokenService,
                jwtTokenService,
                fixedClock
            );

        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    void 로그아웃하면_현재_Refresh_세션을_폐기한다() throws Exception {
        String refreshToken =
            "550e8400-e29b-41d4-a716-446655440000.logout-secret";

        mockMvc.perform(
            post("/api/v1/auth/logout")
                .cookie(new Cookie("refresh_token", refreshToken))
        ).andExpect(status().isNoContent());

        Mockito.verify(refreshTokenService).revoke(refreshToken);
    }

}