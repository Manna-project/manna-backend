package com.manna.auth.service;

import com.manna.auth.dto.AuthUserInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2UserInfoService {
    public AuthUserInfo extract(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> extractGoogleUserInfo(attributes);
            case "kakao" -> extractKakaoUserInfo(attributes);
            default -> throw new IllegalArgumentException(
                "지원하지 않는 OAuth Provider입니다: " + registrationId
            );
        };
    }

    private AuthUserInfo extractGoogleUserInfo(Map<String, Object> attributes) {
        return new AuthUserInfo(
            "google",
            getRequiredValue(attributes, "sub"),
            (String) attributes.get("email"),
            (String) attributes.get("name"),
            (String) attributes.get("picture")
        );
    }

    private AuthUserInfo extractKakaoUserInfo(Map<String, Object> attributes) {
        Map<String, Object> properties =
            (Map<String, Object>) attributes.get("properties");

        Map<String, Object> kakaoAccount =
            (Map<String, Object>) attributes.get("kakao_account");

        return new AuthUserInfo(
            "kakao",
            getRequiredValue(attributes, "id"),
            kakaoAccount == null ? null : (String) kakaoAccount.get("email"),
            properties == null ? null : (String) properties.get("nickname"),
            properties == null ? null : (String) properties.get("profile_image")
        );
    }

    private String getRequiredValue(
        Map<String, Object> attributes,
        String key
    ) {
        Object value = attributes.get(key);

        if (value == null) {
            throw new IllegalArgumentException(
                "OAuth 사용자 정보에 필수 값이 없습니다: " + key
            );
        }

        return String.valueOf(value);
    }
}
