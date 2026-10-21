package com.manna.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 외부 API
    EXTERNAL_API_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 응답이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String message;
}
