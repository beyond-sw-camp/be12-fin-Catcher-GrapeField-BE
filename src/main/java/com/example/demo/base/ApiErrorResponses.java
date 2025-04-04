package com.example.demo.base;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다"),
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다"),
    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다"),
    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다")
})
public @interface ApiErrorResponses {
}


