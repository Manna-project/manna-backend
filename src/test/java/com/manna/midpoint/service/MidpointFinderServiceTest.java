package com.manna.midpoint.service;

import com.manna.midpoint.client.KakaoLocalClient;
import com.manna.midpoint.client.dto.KakaoCategorySearchResponse;
import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.dto.OriginPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;


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
}
