package com.manna.auth.service;

import com.manna.auth.dto.AuthUserInfo;
import com.manna.auth.entity.LoginType;
import com.manna.auth.entity.User;
import com.manna.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public User login(AuthUserInfo userInfo) {
        LoginType loginType = getLoginType(userInfo.provider());

        return userRepository.findByProviderIdAndLoginType(
            userInfo.providerId(),
            loginType
        ).orElseGet(() -> createUser(userInfo, loginType));
    }

    private LoginType getLoginType(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> LoginType.GOOGLE;
            case "kakao" -> LoginType.KAKAO;
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth Provider 입니다." + provider);
        };
    }

    private User createUser(AuthUserInfo userInfo, LoginType loginType) {
        LocalDateTime now = LocalDateTime.now();

        User newUser = User.builder()
            .loginType(loginType)
            .providerId(userInfo.providerId())
            .email(userInfo.email())
            .name(userInfo.name())
            .profileImage(userInfo.profileImageUrl())
            .createdAt(now)
            .updatedAt(now)
            .build();

        return userRepository.save(newUser);
    }
}
