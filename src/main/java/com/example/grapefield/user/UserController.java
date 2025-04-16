package com.example.grapefield.user;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserSignupOauthReq;
import com.example.grapefield.user.model.request.UserInfoDetailReq;
import com.example.grapefield.user.model.request.UserSignupReq;
import com.example.grapefield.user.model.response.UserInfoDetailResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name="1. 회원 기능", description = "회원 가입 및 자신의 정보를 조회할 수 있는 회원 전용 기능")
public class UserController {
  private final UserService userService;

  @Operation(summary="회원가입", description = "회원 가입을 합니다")
  @PostMapping("/signup")
  public ResponseEntity<Boolean> registerUser(
      @Parameter(description="SignupReq 데이터 전송 객체를 사용합니다")
      @ModelAttribute UserSignupReq request) {
    Boolean response = userService.registerUser(request);
    //이미 가입한 메일일 경우 false를 반환
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "소셜 회원가입", description = "소셜 로그인을 이용한 회원가입")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "회원가입 성공",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "가입 성공"))),
          @ApiResponse(responseCode = "409", description = "이메일 중복",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "이미 존재하는 이메일입니다.")))
  })
  @ApiErrorResponses
  @PostMapping("/signup_oauth")
  public ResponseEntity<String> signupOauth(@CookieValue(name = "SUTOKEN") String token,
                                                              @Valid @RequestBody UserSignupOauthReq dto,
                                                              HttpServletResponse response) {

//    UserSignupOauthReq respDto = userService.signupOauth(token, dto);
//    ResponseCookie cookie = ResponseCookie
//            .from("SUTOKEN", "")
//            .path("/")
//            .httpOnly(true)
//            .secure(true)
//            .maxAge(0)
//            .build();
//    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//    if(respDto == null) {
//      return ResponseEntity.badRequest().body(respDto);
//    }
    return ResponseEntity.ok("소셜 회원가입 성공");
  }

  @Operation(summary = "이메일 인증", description = "전송된 이메일을 통해 이메일 인증")
  @GetMapping("/email_verify/{uuid}")
  public boolean verify(@PathVariable String uuid) {
    System.out.println(uuid);
    return userService.verify(uuid);
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "마이페이지", description = "유저가 자신의 정보를 조회합니다")
  @ApiErrorResponses
  @ApiResponse(responseCode = "200",description = "성공적으로 정보를 반환",content = @Content(schema = @Schema(implementation =UserInfoDetailResp.class))
  )
  @GetMapping("/mypage")
  public ResponseEntity<UserInfoDetailResp> getUserInformation(@AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    UserInfoDetailResp userInfo = userService.getUserInfo(user.getEmail());
    return ResponseEntity.ok().body(userInfo);
  }

  @Operation(summary="회원 정보 수정", description="회원 정보 일부를 업데이트합니다.")
  @ApiSuccessResponses
  @ApiErrorResponses
  @PutMapping("/update")
  public ResponseEntity<String> update(
      @RequestBody UserInfoDetailReq request,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("회원 정보 수정 성공");
  }
  
  //TODO : 관심 이벤트 등록, 관심 이벤트 알림 등록, 관심 이벤트 캘린더 등록(???)

  //TODO : 회원 탈퇴 (실제로 db상에서는 삭제x), DB 논의 필요
  @Operation(summary = "회원 탈퇴", description = "기존에 가입한 유저가 탈퇴(본인만 가능, 실제로 DB상에서는 삭제하지 않고 따로 관리)")
  @ApiResponse(responseCode = "200", description = "삭제 성공",
          content = @Content(mediaType = "text/plain",
                  examples = @ExampleObject(value = "삭제 성공")))
  @ApiErrorResponses
  @PutMapping("/delete/{userIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long userIdx, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("탈퇴 성공");
  }

  //TODO: 사용자 신고
}
