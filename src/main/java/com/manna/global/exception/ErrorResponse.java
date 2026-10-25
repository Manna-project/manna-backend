package com.manna.global.exception;

public record ErrorResponse(
        String code, // 에러 종류 (프론트 분기용)
        String message // 사용자에게 보여줄 메시지
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }
}

