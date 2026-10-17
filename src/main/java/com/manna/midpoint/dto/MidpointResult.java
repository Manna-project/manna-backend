package com.manna.midpoint.dto;

import java.util.List;

public record MidpointResult(
        String stationName, // 역명
        double lat,
        double lng,
        double avgDistanceKm, // 평균 이동거리
        List<TravelTime> travelTimes // 친구별 소요시간
) {
    public MidpointResult {
        travelTimes = List.copyOf(travelTimes);
    }
}
