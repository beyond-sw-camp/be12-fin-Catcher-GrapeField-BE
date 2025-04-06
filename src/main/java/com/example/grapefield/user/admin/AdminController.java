package com.example.grapefield.user.admin;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.response.UserInfoListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "BearerAuth")
@Tag(name="2. 관리자 전용 기능", description = "공연 및 전시를 등록하고 가입한 회원을 제재하거나 게시글, 댓글 등을 삭제)")
public class AdminController {
    @Operation(summary = "회원 목록 조회", description = "관리자가 전체 회원 목록을 조회합니다")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserInfoListResp>> getUserList(
            @AuthenticationPrincipal User user) {
        List<UserInfoListResp> dummyList = List.of(
                new UserInfoListResp("gildong", "gildong@example.com", LocalDateTime.now(), "user"),
                new UserInfoListResp("admin", "admin@example.com", LocalDateTime.now(), "admin")
        );
        return ResponseEntity.ok(dummyList);
    }
    
    //TODO : 신고 목록 확인
    //TODO : 신고 처리
}
