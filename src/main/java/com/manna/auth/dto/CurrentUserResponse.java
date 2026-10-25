package com.manna.auth.dto;

import com.manna.auth.entity.User;

import java.util.UUID;

public record CurrentUserResponse(
    UUID entityId,
    String email,
    String name,
    String nickname,
    String profileImage
) {
    public static CurrentUserResponse from(User user) {
        return new CurrentUserResponse(
            user.getEntityId(),
            user.getEmail(),
            user.getName(),
            user.getNickname(),
            user.getProfileImage()
        );
    }
}
