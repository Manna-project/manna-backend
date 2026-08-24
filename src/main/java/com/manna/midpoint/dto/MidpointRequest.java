package com.manna.midpoint.dto;

import java.util.List;

public record MidpointRequest(
        List<OriginPoint> originPoints
) {
}
