package com.example.grapefield.user;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.common.ImageService;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserSignupOauthReq;
import com.example.grapefield.user.model.request.UserInfoDetailReq;
import com.example.grapefield.user.model.request.UserSignupReq;
import com.example.grapefield.user.model.response.UserInfoDetailResp;
import com.example.grapefield.user.repository.UserRepository;
import com.example.grapefield.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "1. 회원 기능", description = "회원 가입 및 자신의 정보를 조회할 수 있는 회원 전용 기능")
public class UserController {
    private final UserService userService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private boolean aBoolean;

    @Operation(summary = "회원가입", description = "회원 가입을 합니다")
    @PostMapping("/signup")
    public ResponseEntity<Boolean> registerUser(
            @Parameter(description = "SignupReq 데이터 전송 객체를 사용합니다")
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
    @ApiResponse(responseCode = "200", description = "성공적으로 정보를 반환", content = @Content(schema = @Schema(implementation = UserInfoDetailResp.class))
    )
    @GetMapping("/mypage")
    public ResponseEntity<UserInfoDetailResp> getUserInformation(@AuthenticationPrincipal CustomUserDetails principal) {
        User user = (principal != null) ? principal.getUser() : null;
        UserInfoDetailResp userInfo = userService.getUserInfo(user.getEmail());
        return ResponseEntity.ok().body(userInfo);
    }

    @PostMapping("/password_verify")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String rawPassword = request.get("password");
        if (rawPassword == null) {
            return ResponseEntity.badRequest().body("비밀번호가 필요합니다");
        }

        boolean result = userService.verifyPassword(rawPassword, userDetails.getUsername());
        System.out.println("비밀번호 확인 : " + result);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 이름, 전화번호를 업데이트합니다.")
    @ApiSuccessResponses
    @ApiErrorResponses
    @PutMapping("/update")
    public ResponseEntity<?> update(
            @RequestBody UserInfoDetailReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        boolean result = userService.updateUser(request, user);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "프로필 이미지 업로드", description = "사용자 프로필 이미지를 업로드합니다.")
    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 파일이 필요합니다");
        }

        try {
            String imageUrl = imageService.userProfileUpload(file);

            // 이미지 URL만 업데이트
            User user = userDetails.getUser();
            user.setProfileImg(imageUrl);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "imageUrl", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 실패: " + e.getMessage());
        }
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
