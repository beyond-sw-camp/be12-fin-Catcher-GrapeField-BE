package com.example.demo.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="회원가입 요청 body")
public class UserSignupReq {
  @NotBlank
  @Schema(description="실명(문자열): 필수", example = "gildong")
  @Pattern(regexp = "[0-9A-Za-z가-힣]+", message="signup wrong name")
  private String username;
  @NotBlank
  @Schema(description="이메일(문자열): 필수", example = "example@example.com")
  @NotBlank
  @Email(message = "signup wrong email")
  private String email;
  @Schema(description="패스워드(문자열): 8자 이상 영문, 숫자, 일부 특수문자 사용" )
  @NotBlank
  @Pattern(regexp = "[0-9A-Za-z!~$*]{8,}", message="signup wrong pass")
  private String password;
  @Schema(description = "프로필 이미지 URL(문자열): 선택, 이미지 1장을 업로드하여 DB에 저장된 경로", example = "/sample/images/profile/profile1.jpg")
  private String profileImg;
  @Schema(description = "전화번호(문자열): 선택, 하이픈 없이 숫자만 허용", example = "01012345678")
  private String phoneNumber;
}
