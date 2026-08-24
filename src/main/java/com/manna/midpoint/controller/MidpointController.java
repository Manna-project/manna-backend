package com.manna.midpoint.controller;

import com.manna.midpoint.dto.MidpointRequest;
import com.manna.midpoint.dto.MidpointResult;
import com.manna.midpoint.service.MidpointFinderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/midpoint")
@RequiredArgsConstructor
public class MidpointController {

    private final MidpointFinderService midpointFinderService;

    @PostMapping
    public MidpointResult findMidpoint(@RequestBody MidpointRequest midpointRequest) {
        return midpointFinderService.findMidpoint(midpointRequest.originPoints());
    }
}