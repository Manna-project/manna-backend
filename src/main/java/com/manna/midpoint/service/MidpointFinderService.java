package com.manna.midpoint.service;

import com.manna.midpoint.client.KakaoLocalClient;
import com.manna.midpoint.client.dto.KakaoCategorySearchResponse;
import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
import com.manna.midpoint.dto.TravelTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MidpointFinderService {

    private final MidpointService midpointService;
    private final KakaoLocalClient kakaoLocalClient;

    public MidpointFinderService(MidpointService midpointService, KakaoLocalClient kakaoLocalClient) {
        this.midpointService = midpointService;
        this.kakaoLocalClient = kakaoLocalClient;
    }

    public MidpointResult findMidpoint(List<OriginPoint> originPoints) {
        // 1. 중간지점 계산
        MidpointResult calculated = midpointService.calculateMidpoint(originPoints);

        Optional<KakaoCategorySearchResponse.Document> nearestStation = kakaoLocalClient.findNearestSubwayStation(calculated.lat(), calculated.lng());

        // 2. 도착지 정하기: 역이 있으면 역, 없으면 계산 좌표
        String stationName = nearestStation
                .map(KakaoCategorySearchResponse.Document::placeName)
                .orElse(calculated.stationName());
        double destLat = nearestStation
                .map(station -> Double.parseDouble(station.lat()))
                .orElse(calculated.lat());
        double destLng = nearestStation
                .map(station -> Double.parseDouble(station.lng()))
                .orElse(calculated.lng());

        // 3. 친구별 소요시간 계산
        List<TravelTime> travelTimes = calculateTravelTimes(originPoints, destLat, destLng);

        // 4. 최종
        return new MidpointResult(stationName, destLat, destLng, calculated.avgDistanceKm(), travelTimes);
    }

    private List<TravelTime> calculateTravelTimes(List<OriginPoint> originPoints, double destLat, double destLng) {
        return originPoints.stream()
                .map(origin -> new TravelTime(origin.name(), calculateTravelMinutes(origin, destLat, destLng)))
                .toList();
    }

    private Integer calculateTravelMinutes(OriginPoint origin, double destLat, double destLng) {
        return kakaoLocalClient.findFastestTransitTimeSeconds(origin.lat(), origin.lng(), destLat, destLng)
                .map(seconds -> (int) Math.ceil(seconds / 60.0))
                .orElse(null);
    }
}
