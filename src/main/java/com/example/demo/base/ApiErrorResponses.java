package com.example.demo.base;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
            @ExampleObject(value = "{\"status\":400,\"message\":\"잘못된 요청입니다\",\"timestamp\":\"2025-04-04T03:17:51\"}")
        })),
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
            @ExampleObject(value = "{\"status\":401,\"message\":\"인증되지 않은 사용자입니다\",\"timestamp\":\"2025-04-04T03:17:51\"}")
        })),
    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
            @ExampleObject(value = "{\"status\":403,\"message\":\"접근 권한이 없습니다\",\"timestamp\":\"2025-04-04T03:17:51\"}")
        })),
    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
            @ExampleObject(value = "{\"status\":500,\"message\":\"서버 오류가 발생했습니다\",\"timestamp\":\"2025-04-04T03:17:51\"}")
        }))
})
public @interface ApiErrorResponses {
}

