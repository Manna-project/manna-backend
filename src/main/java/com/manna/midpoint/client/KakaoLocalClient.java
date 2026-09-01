package com.manna.midpoint.client;

import com.manna.midpoint.client.dto.KakaoCategorySearchResponse;
import com.manna.midpoint.client.dto.KakaoTransitRouteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class KakaoLocalClient {

    private static final String SUBWAY_CATEGORY_CODE = "SW8";
    private static final int SEARCH_RADIUS_METERS = 3000;

    private final RestClient restClient;
    private final String apiKey;

    public KakaoLocalClient(
            RestClient.Builder restClientBuilder,
            @Value("${kakao.api.rest-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://dapi.kakao.com")
                .build();
        this.apiKey = apiKey;
    }

    public Optional<KakaoCategorySearchResponse.Document> findNearestSubwayStation(double lat, double lng) {
        KakaoCategorySearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/category.json")
                        .queryParam("category_group_code", SUBWAY_CATEGORY_CODE)
                        .queryParam("x", lng)
                        .queryParam("y", lat)
                        .queryParam("radius", SEARCH_RADIUS_METERS)
                        .queryParam("sort", "distance")
                        .build())
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .body(KakaoCategorySearchResponse.class);

        if (response == null || response.documents().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(response.documents().get(0));
    }

    public Optional<Integer> findFastestTransitTimeSeconds(
            double startLat, double startLng,
            double endLat, double endLng
    ) {
        KakaoTransitRouteResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/routing/publictraffic")
                        .queryParam("start_x", startLng)
                        .queryParam("start_y", startLng)
                        .queryParam("end_x", endLng)
                        .queryParam("end_y", endLat)
                        .build())
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .body(KakaoTransitRouteResponse.class);

        if (response == null || !"OK".equals(response.status()) || response.routes().isEmpty()) {
            return Optional.empty();
        }

        return response.routes().stream()
                .mapToInt(route -> route.properties().totalTimeSeconds())
                .min()
                .stream()
                .boxed()
                .findFirst();
    }
}
