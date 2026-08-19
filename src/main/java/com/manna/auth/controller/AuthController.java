package com.manna.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class AuthController {
    private static final Set<String> SUPPORTED_PROVIDERS = Set.of("google", "kakao");

    @GetMapping("/{provider}")
    public void redirectToProvider(@PathVariable String provider, HttpServletResponse response) throws IOException {
        if (!SUPPORTED_PROVIDERS.contains(provider)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원하지 않는 OAuth Provider 입니다.");
            return;
        }

        response.sendRedirect("/oauth2/authorization/" + provider);
    }
}
