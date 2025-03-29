package com.example.demo.base;

import com.example.demo.base.model.request.BaseReqDto;
import com.example.demo.base.model.response.BaseRespDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/base")
public class BaseController {
    private final BaseService baseService;

    @Operation(summary = "등록", description = "등록 기능 기본 양식")
    @PostMapping("/post")
    public ResponseEntity<Long> register(@RequestBody BaseReqDto dto) {
        Long resp = baseService.register(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/get/{idx}")
    @Operation(summary = "단일 조회", description = "단일 조회 기본 양식")
    public ResponseEntity<BaseRespDto> instance(@RequestParam Long idx){
        BaseRespDto instance = baseService.instance(idx);
        return ResponseEntity.ok(instance);
    }
}
