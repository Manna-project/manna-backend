package com.manna.auth.dto;

public record AuthUserInfo(
    String provider,
    String providerId,
    String email,
    String name,
    String profileImageUrl
) {
}
