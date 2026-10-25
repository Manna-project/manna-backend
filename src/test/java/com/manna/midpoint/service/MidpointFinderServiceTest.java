package com.manna.midpoint.service;

import com.manna.global.exception.BusinessException;
import com.manna.global.exception.ErrorCode;
import com.manna.midpoint.client.KakaoLocalClient;
import com.manna.midpoint.client.dto.KakaoCategorySearchResponse;
import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
import com.manna.midpoint.dto.TravelTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MidpointFinderServiceTest {

    private final MidpointService midpointService = new MidpointService();

    @Mock
    private KakaoLocalClient kakaoLocalClient;

    @Test
    void 지하철역_있으면_정보저장() {
        OriginPoint kim = new OriginPoint("김", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이", 37.5, 127.4);

        KakaoCategorySearchResponse.Document station = new KakaoCategorySearchResponse.Document(
                "홍대입구역", "127.2", "37.5", "450"
        );

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(station));

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        MidpointResult result = finderService.findMidpoint(List.of(kim, lee));

        assertThat(result.stationName()).isEqualTo("홍대입구역");
        assertThat(result.lat()).isEqualTo(37.5);
        assertThat(result.lng()).isEqualTo(127.2);
    }

    @Test
    void 지하철역_없으면_계산좌표_반환() {
        OriginPoint kim = new OriginPoint("김OO", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이OO", 37.5, 127.4);

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.empty());

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        MidpointResult result = finderService.findMidpoint(List.of(kim, lee));

        assertThat(result.stationName()).isNull();
        assertThat(result.lat()).isEqualTo(37.5);
        assertThat(result.lng()).isEqualTo(127.2);
    }

    @Test
    void 소요시간_초를_분으로_올림() {
        OriginPoint kim = new OriginPoint("김OO", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이OO", 37.5, 127.4);

        KakaoCategorySearchResponse.Document station = new KakaoCategorySearchResponse.Document(
                "홍대입구역", "127.2", "37.5", "450"
        );

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(station));
        when(kakaoLocalClient.findFastestTransitTimeSeconds(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Optional.of(125)); // 2분 5초

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        MidpointResult result = finderService.findMidpoint(List.of(kim, lee));

        assertThat(result.travelTimes())
                .extracting(TravelTime::durationMinutes)
                .containsExactly(3, 3); // 125초 -> 올림해서 3분
    }

    @Test
    void 경로_조회_실패한_친구만_null() {
        OriginPoint kim = new OriginPoint("김", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이", 37.5, 127.4);

        KakaoCategorySearchResponse.Document station = new KakaoCategorySearchResponse.Document(
                "홍대입구역", "127.2", "37.5", "450"
        );

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(station));
        // 김: 600초(10분) 성공
        when(kakaoLocalClient.findFastestTransitTimeSeconds(eq(37.5), eq(127.0), anyDouble(), anyDouble()))
                .thenReturn(Optional.of(600));
        // 이: 경로 없음
        when(kakaoLocalClient.findFastestTransitTimeSeconds(eq(37.5), eq(127.4), anyDouble(), anyDouble()))
                .thenReturn(Optional.empty());

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        MidpointResult result = finderService.findMidpoint(List.of(kim, lee));

        assertThat(result.travelTimes()).hasSize(2); // 실패해도 친구 수는 그대로
        assertThat(result.travelTimes())
                .extracting(TravelTime::originName, TravelTime::durationMinutes)
                .containsExactly(
                        tuple("김", 10),
                        tuple("이", null)
                );
    }


    @Test
    void 역_없으면_계산좌표로_소요시간_조회() {
        OriginPoint kim = new OriginPoint("김", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이", 37.5, 127.4);

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.empty());
        when(kakaoLocalClient.findFastestTransitTimeSeconds(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Optional.of(600));

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        MidpointResult result = finderService.findMidpoint(List.of(kim, lee));

        // 도착지가 계산 좌표(result.lat(), result.lng())로 친구 2명분 호출됐는지
        verify(kakaoLocalClient, times(2)).findFastestTransitTimeSeconds(
                anyDouble(), anyDouble(), eq(result.lat()), eq(result.lng())
        );
        assertThat(result.travelTimes())
                .extracting(TravelTime::durationMinutes)
                .containsExactly(10, 10);
    }

    @Test
    void 역_조회_중_카카오_장애면_예외_전파() {
        OriginPoint kim = new OriginPoint("김", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이", 37.5, 127.4);

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenThrow(new BusinessException(ErrorCode.EXTERNAL_API_UNAVAILABLE));

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        assertThatThrownBy(() -> finderService.findMidpoint(List.of(kim, lee)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXTERNAL_API_UNAVAILABLE);
    }

    @Test
    void 소요시간_조회_중_카카오_장애면_null이_아니라_예외_전파() {
        OriginPoint kim = new OriginPoint("김", 37.5, 127.0);
        OriginPoint lee = new OriginPoint("이", 37.5, 127.4);

        KakaoCategorySearchResponse.Document station = new KakaoCategorySearchResponse.Document(
                "홍대입구역", "127.2", "37.5", "450"
        );

        when(kakaoLocalClient.findNearestSubwayStation(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(station));
        when(kakaoLocalClient.findFastestTransitTimeSeconds(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenThrow(new BusinessException(ErrorCode.EXTERNAL_API_UNAVAILABLE));

        MidpointFinderService finderService = new MidpointFinderService(midpointService, kakaoLocalClient);

        assertThatThrownBy(() -> finderService.findMidpoint(List.of(kim, lee)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXTERNAL_API_UNAVAILABLE);
    }


}
