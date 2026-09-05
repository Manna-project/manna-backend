package com.manna.midpoint.service;

public class KoreaMainlandValidator {

    // 대한민국 본토 4극점 기준
    // 십진법 = 도 + 분/60 + 초/3600
    // 남단 : 전라남도 해남군 송지면 송호리 산43-16, 북위 34도 17분 32초
    private static final double MIN_LAT = 34.2922;

    // 북단 : 강원도 고성군 현내면 대강리, 북위 38도 36분 40초
    private static final double MAX_LAT = 38.61111;

    // 서단 : 충청남도 태안군 소원면 모항리 997-6, 동경 126도 06분 42초
    private static final double MIN_LNG = 126.1117;

    // 동단 : 경상북도 구룡포읍 석병리 산 135, 동경 129도 35분 05초
    private static final double MAX_LNG = 129.5847;

    public static boolean isMainland(double lat, double lng) {
        return lat >= MIN_LAT && lat <= MAX_LAT && lng >= MIN_LNG && lng <= MAX_LNG;
    }
}
