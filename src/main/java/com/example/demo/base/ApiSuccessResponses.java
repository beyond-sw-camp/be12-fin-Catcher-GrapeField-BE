package com.example.demo.base;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "응답이 정상적으로 반환되었습니다.")
})
public @interface ApiSuccessResponses {
}