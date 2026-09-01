package com.manna.midpoint.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoTransitRouteResponse(
        String status,
        List<Route> routes
) {
    public record Route(
            RouteProperties properties
    ) {
    }

    public record RouteProperties(
            String type,
            @JsonProperty("totalDistance") int totalDistanceMeters,
            @JsonProperty("totalTime") int totalTimeSeconds
    ){

    }
}
