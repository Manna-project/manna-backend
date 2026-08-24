package com.manna.midpoint.client.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoCategorySearchResponse(
        List<Document> documents
) {
    public record Document(
            @JsonProperty("place_name") String placeName,
            @JsonProperty("x") String lng,
            @JsonProperty("y") String lat,
            @JsonProperty("distance") String distanceMeters
    ) {

    }
}
