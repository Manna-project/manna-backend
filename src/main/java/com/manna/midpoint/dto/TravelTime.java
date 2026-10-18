package com.manna.midpoint.dto;

public record TravelTime(
        String originName, // 친구 이름
        Integer durationMinutes // 소요시간(분), 경로를 못 찾으면 NULL
) {
}
