package com.example.demo.base;

import com.example.demo.base.model.request.BaseReqDto;
import com.example.demo.base.model.response.BaseRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/base")
public class BaseController {
    private final BaseService baseService;

    //등록
    @PostMapping("/post")
    public ResponseEntity<Long> register(@RequestBody BaseReqDto dto) {
        Long resp = baseService.register(dto);
        return ResponseEntity.ok(resp);
    }

    //단일 조회
    @GetMapping("/get/{idx}")
    public ResponseEntity<BaseRespDto> instance(@RequestParam Long idx){
        BaseRespDto instance = baseService.instance(idx);
        return ResponseEntity.ok(instance);
    }
}
