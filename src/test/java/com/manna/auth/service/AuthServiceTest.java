package com.manna.auth.service;

import com.manna.auth.dto.AuthUserInfo;
import com.manna.auth.entity.LoginType;
import com.manna.auth.entity.User;
import com.manna.auth.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void 기존_사용자가_있으면_해당_사용자를_반환한다() {
        AuthUserInfo userInfo = new AuthUserInfo(
            "google",
            "google-provider-id",
            "test@example.com",
            "테스트 사용자",
            "https://example.com/profile.png"
        );

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
            .loginType(LoginType.GOOGLE)
            .providerId(userInfo.providerId())
            .email(userInfo.email())
            .name(userInfo.name())
            .profileImage(userInfo.profileImageUrl())
            .createdAt(now)
            .updatedAt(now)
            .build();

        when(userRepository.findByProviderIdAndLoginType(userInfo.providerId(), LoginType.GOOGLE))
            .thenReturn(Optional.of(user));

        User result = authService.login(userInfo);

        assertThat(result).isSameAs(user);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 사용자가_없으면_새로운_사용자를_저장한다() {
        AuthUserInfo userInfo = new AuthUserInfo(
            "kakao",
            "kakao-provider-id",
            "kakao@example.com",
            "카카오 사용자",
            "https://example.com/kakao.png"
        );

        when(userRepository.findByProviderIdAndLoginType("kakao-provider-id", LoginType.KAKAO))
            .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));


        User result = authService.login(userInfo);

        assertThat(result.getLoginType()).isEqualTo(LoginType.KAKAO);
        assertThat(result.getProviderId()).isEqualTo("kakao-provider-id");
        assertThat(result.getEmail()).isEqualTo("kakao@example.com");
        assertThat(result.getName()).isEqualTo("카카오 사용자");
        assertThat(result.getProfileImage()).isEqualTo("https://example.com/kakao.png");

        assertThat(result.getEntityId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void 지원하지_않는_provider면_예외가_발생한다() {
        AuthUserInfo userInfo = new AuthUserInfo(
            "naver",
            "naver-provider-id",
            "naver@example.com",
            "네이버 사용자",
            null
        );

        assertThatThrownBy(() -> authService.login(userInfo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("지원하지 않는 OAuth Provider 입니다: naver");

        verifyNoInteractions(userRepository);
    }

    @Test
    void entityId로_사용자를_조회한다() {
        UUID entityId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        User existingUser = User.builder()
            .loginType(LoginType.GOOGLE)
            .providerId("provider-id")
            .email("test@example.com")
            .name("테스트 사용자")
            .createdAt(now)
            .updatedAt(now)
            .build();

        when(userRepository.findByEntityId(entityId))
            .thenReturn(Optional.of(existingUser));

        User result = authService.getUser(entityId);

        assertThat(result).isSameAs(existingUser);
    }

    @Test
    void entityId에_해당하는_사용자가_없으면_예외가_발생한다() {
        UUID entityId = UUID.randomUUID();

        when(userRepository.findByEntityId(entityId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getUser(entityId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("사용자를 찾을 수 없습니다.");
    }
}