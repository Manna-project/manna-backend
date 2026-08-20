package com.manna.midpoint.service;

import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MidpointServiceTest {

    private final MidpointService midpointService = new MidpointService();

    @Test
    void 두명_중간지점_반환() {

        OriginPoint kim = new OriginPoint("김00", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이00", 37.5, 127.4);

        MidpointResult result = midpointService.calculateMidpoint(List.of(kim, lee));

        assertThat(result.lat()).isCloseTo(37.5, Offset.offset(0.001));
        assertThat(result.lng()).isCloseTo(127.2, Offset.offset(0.001));
    }

    @Test
    void 같은좌표_두명_중간지점_반환() {

        OriginPoint kim = new OriginPoint("김00", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이00", 37.5, 127.0);

        MidpointResult result = midpointService.calculateMidpoint(List.of(kim, lee));

        assertThat(result.lat()).isCloseTo(37.5, Offset.offset(0.001));
        assertThat(result.lng()).isCloseTo(127.0, Offset.offset(0.001));
        assertThat(result.avgDistanceKm()).isCloseTo(0.0, Offset.offset(0.0001));
    }

    @Test
    void 출발지_null() {
        assertThatThrownBy(() -> midpointService.calculateMidpoint(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 출발지_비어있으면() {
        assertThatThrownBy(() -> midpointService.calculateMidpoint(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 출발지_1명뿐() {
        OriginPoint kim = new OriginPoint("김OO", 37.5, 127.0);

        assertThatThrownBy(() -> midpointService.calculateMidpoint(List.of(kim)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 평균이동거리_0이상() {
        OriginPoint kim = new OriginPoint("김OO", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이OO", 37.6, 127.1);
        OriginPoint park = new OriginPoint("박OO", 37.4, 126.9);

        MidpointResult result = midpointService.calculateMidpoint(List.of(kim, lee, park));

        assertThat(result.avgDistanceKm()).isGreaterThan(0.0);
    }

    @Test
    void 세명중_두명의_좌표가_같음() {
        OriginPoint kim = new OriginPoint("김OO", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이OO", 37.5, 127.0);
        OriginPoint park = new OriginPoint("박OO", 38.0, 128.0);

        MidpointResult result = midpointService.calculateMidpoint(List.of(kim, lee, park));

        assertThat(result.lat()).isCloseTo(37.5, Offset.offset(0.001));
        assertThat(result.lng()).isCloseTo(127.0, Offset.offset(0.001));
    }

    @Test
    void 서울_부산() {
        OriginPoint seoul = new OriginPoint("서울", 37.5665, 126.9780);
        OriginPoint busan = new OriginPoint("부산", 35.1796, 129.0756);

        MidpointResult result = midpointService.calculateMidpoint(List.of(seoul, busan));

        assertThat(result.lat()).isNotNaN();
        assertThat(result.lng()).isNotNaN();
        assertThat(result.avgDistanceKm()).isGreaterThan(0.0);
    }

    @Test
    void 섬_예외() {
        OriginPoint seoul = new OriginPoint("서울", 37.5665, 126.9780);
        OriginPoint jeju = new OriginPoint("제주", 33.4996, 126.5312);

        assertThatThrownBy(() -> midpointService.calculateMidpoint(List.of(seoul, jeju))).isInstanceOf(IllegalArgumentException.class);
        
    }
}