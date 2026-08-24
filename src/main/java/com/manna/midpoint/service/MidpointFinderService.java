package com.manna.midpoint.service;

import com.manna.midpoint.client.KakaoLocalClient;
import com.manna.midpoint.client.dto.KakaoCategorySearchResponse;
import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
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
        MidpointResult calculated = midpointService.calculateMidpoint(originPoints);

        Optional<KakaoCategorySearchResponse.Document> nearestStation = kakaoLocalClient.findNearestSubwayStation(calculated.lat(), calculated.lng());

        return nearestStation
                .map(station -> new MidpointResult(
                        station.placeName(),
                        Double.parseDouble(station.lat()),
                        Double.parseDouble(station.lng()),
                        calculated.avgDistanceKm()
                )).orElse(calculated);
    }
}
