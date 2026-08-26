package com.manna.auth.controller;

import com.manna.auth.dto.CurrentUserResponse;
import com.manna.auth.entity.User;
import com.manna.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(Authentication authentication) {
        UUID entityId = UUID.fromString(authentication.getName());

        User user = authService.getUser(entityId);

        return CurrentUserResponse.from(user);
    }
}
