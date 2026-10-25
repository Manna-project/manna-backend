package com.manna.auth.service;

import com.manna.auth.dto.AuthUserInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OAuth2UserInfoServiceTest {

    private final OAuth2UserInfoService service = new  OAuth2UserInfoService();

    @Test
    void Google_사용자_정보를_추출한다() {
        Map<String, Object> attributes = Map.of(
            "sub", "google-provider-id",
            "email", "google@example.com",
            "name", "Google User",
            "picture", "https://example.com/google.png"
        );

        AuthUserInfo result = service.extract("google", attributes);

        assertThat(result).isEqualTo(
            new AuthUserInfo(
                "google",
                "google-provider-id",
                "google@example.com",
                "Google User",
                "https://example.com/google.png"
            )
        );
    }

    @Test
    void Kakao_사용자_정보를_추출한다() {
        Map<String, Object> attributes = Map.of(
            "id", 123456789L,
            "properties", Map.of(
                "nickname", "Kakao User",
                "profile_image", "https://example.com/kakao.png"
            )       ,
            "kakao_account", Map.of(
                "email", "kakao@example.com"
            )
        );

        AuthUserInfo result = service.extract("kakao", attributes);

        assertThat(result).isEqualTo(
            new AuthUserInfo(
                "kakao",
                "123456789",
                "kakao@example.com",
                "Kakao User",
                "https://example.com/kakao.png"
            )
        );
    }

    @Test
    void Google_sub가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> service.extract("google", Map.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sub");
    }

    @Test
    void 지원하지_않는_provider면_예외가_발생한다() {
        assertThatThrownBy(() -> service.extract("naver", Map.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("지원하지 않는 OAuth Provider입니다: naver");
    }

}