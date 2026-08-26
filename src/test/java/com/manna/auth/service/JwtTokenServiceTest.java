package com.manna.auth.service;

import com.manna.auth.entity.LoginType;
import com.manna.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private User user;

    @BeforeEach
    void setUp() {
        String testSecret = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());

        jwtTokenService = new JwtTokenService(testSecret);

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
            .loginType(LoginType.GOOGLE)
            .providerId("test-provider-id")
            .email("test@example.com")
            .name("테스트 사용자")
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Test
    void Access_Token을_생성한다() {
        String token = jwtTokenService.createAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenService.validateToken(token)).isTrue();
        assertThat(jwtTokenService.isAccessToken(token)).isTrue();
        assertThat(jwtTokenService.isRefreshToken(token)).isFalse();
        assertThat(jwtTokenService.getSubject(token))
            .isEqualTo(user.getEntityId().toString());
    }

    @Test
    void Refresh_Token을_생성한다() {
        String token = jwtTokenService.createRefreshToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenService.validateToken(token)).isTrue();
        assertThat(jwtTokenService.isAccessToken(token)).isFalse();
        assertThat(jwtTokenService.isRefreshToken(token)).isTrue();
        assertThat(jwtTokenService.getSubject(token))
            .isEqualTo(user.getEntityId().toString());
    }

    @Test
    void 다른_키로_서명된_토큰은_유효하지_않다() {
        String otherSecret = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());

        JwtTokenService otherTokenService = new JwtTokenService(otherSecret);

        String token = otherTokenService.createAccessToken(user);

        assertThat(jwtTokenService.validateToken(token)).isFalse();
    }
}