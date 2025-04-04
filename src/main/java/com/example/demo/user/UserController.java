package com.example.demo.user;

import com.example.demo.base.ApiErrorResponses;
import com.example.demo.base.ApiSuccessResponses;
import com.example.demo.user.model.entity.User;
import com.example.demo.user.model.request.UserInfoDetailReq;
import com.example.demo.user.model.request.UserSignupReq;
import com.example.demo.user.model.response.UserInfoDetailResp;
import com.example.demo.user.model.response.UserInfoListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name="회원 기능", description = "회원 가입, 유저 정보 조회(자기자신 혹은 관리자가 다른 유저들을 관리)")
public class UserController {
  @Operation(summary="회원가입", description = "회원 가입을 합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "회원가입 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "가입 성공"))),
      @ApiResponse(responseCode = "409", description = "이메일 중복",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "이미 존재하는 이메일입니다.")))
  })
  @ApiErrorResponses
  @PostMapping("/signup")
  public ResponseEntity<String> signup(
      @Parameter(description="SignupReq 데이터 전송 객체를 사용합니다")
      @RequestBody UserSignupReq request) {
    return ResponseEntity.ok("가입 성공");
  }

  @Operation(summary = "마이페이지", description = "유저가 자신의 정보를 조회합니다")
  @ApiErrorResponses
  @ApiResponse(responseCode = "200",description = "성공적으로 정보를 반환",content = @Content(schema = @Schema(implementation =UserInfoDetailResp.class))
  )
  @GetMapping("/mypage")
  public ResponseEntity<UserInfoDetailResp> getUserInformation(@AuthenticationPrincipal User user) {
    UserInfoDetailResp dummy = new UserInfoDetailResp();
    return ResponseEntity.ok().body(dummy);
  }

  @Operation(summary="회원 정보 수정", description="회원 정보 일부를 업데이트합니다.")
  @ApiSuccessResponses
  @ApiErrorResponses
  @PutMapping("/update")
  public ResponseEntity<String> update(
      @Parameter(description="UserUpdateReq 데이터 전송 객체를 사용합니다")
      @RequestBody UserInfoDetailReq request,
      @AuthenticationPrincipal User user) {
    UserInfoDetailReq dummy = new UserInfoDetailReq();
    return ResponseEntity.ok("회원 정보 수정 성공");
  }

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

}
