package com.manna.midpoint.dto;

public record MidpointResult(
        String stationName, // 역명
        double lat,
        double lng,
        double avgDistanceKm // 평균 이동거리
) {
}
