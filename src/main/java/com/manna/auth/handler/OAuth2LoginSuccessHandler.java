package com.manna.auth.handler;

import com.manna.auth.dto.AuthUserInfo;
import com.manna.auth.service.AuthService;
import com.manna.auth.service.OAuth2UserInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final AuthService authService;
    private final OAuth2UserInfoService oauth2UserInfoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        AuthUserInfo userInfo = oauth2UserInfoService.extract(registrationId, oauth2User.getAttributes());

        authService.login(userInfo);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
