package com.manna.midpoint.service;

import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MidpointService {

    private static final double EARTH_RADIUS_KM = 6371.0;  // 지구 반지름 상수
    private static final int MAX_ITERATIONS = 100;  // 기하 중앙값을 구하는 Weiszfeld 알고리즘 최대 반복 횟수
    private static final double CONVERGENCE_THRESHOLD = 1e-8;  // 중간지점 좌표의 이동량이 해당 값보다 작아지면 반복 중지 (수렴 판단)
    private static final int MIN_ORIGIN_COUNT = 2;

    public MidpointResult calculateMidpoint(List<OriginPoint> originPoints) {

        if (originPoints == null || originPoints.isEmpty()) {
            throw new IllegalArgumentException("출발지를 입력해주세요.");
        }
        if (originPoints.size() < MIN_ORIGIN_COUNT) {
            throw new IllegalArgumentException("최소 2명 이상의 출발지를 입력해주세요.");
        }
        for (OriginPoint o : originPoints) {
            if (!KoreaMainlandValidator.isMainland(o.lat(), o.lng())) {
                throw new IllegalArgumentException("해당 위치는 지원하고 있지 않습니다.");
            }
        }

        // 초기값: 단순 평균 -> Weiszfeld 알고리즘 반복
        double lat = originPoints.stream().mapToDouble(OriginPoint::lat).average().orElseThrow();
        double lng = originPoints.stream().mapToDouble(OriginPoint::lng).average().orElseThrow();

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double weightSum = 0.0;  // 가중치들의 총합
            double weightedLatSum = 0.0;  // 가중치를 곱한 위도 값들의 총합
            double weightedLngSum = 0.0;  // 가중치를 곱한 경도 값들의 총합

            // 가중치 계산
            for (OriginPoint o : originPoints) {
                double distance = haversine(lat, lng, o.lat(), o.lng());
                double weight = 1.0 / Math.max(distance, 1e-9);

                weightSum += weight;
                weightedLatSum += o.lat() * weight;
                weightedLngSum += o.lng() * weight;
            }

            double newLat = weightedLatSum / weightSum;
            double newLng = weightedLngSum / weightSum;

            // 이동량이 임계값보다 작으면 수렴 -> 종료
            if (Math.abs(newLat - lat) < CONVERGENCE_THRESHOLD && Math.abs(newLng - lng) < CONVERGENCE_THRESHOLD) {
                lat = newLat;
                lng = newLng;
                break;
            }

            lat = newLat;
            lng = newLng;
        }

        double finalLat = lat;
        double finalLng = lng;
        double avgDistanceKm = originPoints.stream()
                .mapToDouble(o -> haversine(finalLat, finalLng, o.lat(), o.lng()))
                .average().orElse(0.0);

        return new MidpointResult(null, finalLat, finalLng, avgDistanceKm);
    }

    // 두 지점 사이가 실제 거리
    private double haversine(double lat1, double lng1, double lat2, double lng2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
