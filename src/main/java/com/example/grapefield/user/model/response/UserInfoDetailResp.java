package com.example.grapefield.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="회원 상세 정보 응답")
public class UserInfoDetailResp {
  @Schema(description="실명(문자열)", example = "gildong")
  private String username;
  @Schema(description="이메일(문자열)", example = "example@example.com")
  private String email;
  @Schema(description="패스워드(문자열)로 암호화처리되어 ***표시", example = "*********")
  private String password;
  @Schema(description = "전화번호(문자열), 하이픈 없이 숫자만 허용", example = "01012345678")
  private String phoneNumber;
  @Schema(description = "프로필 이미지 URL(문자열), 없을 경우 기본 이미지 표시", example = "/sample/images/profile/profile1.jpg")
  private String profileImg;
  @Schema(description = "가입일", example = "2025-04-01T14:30:00")
  private LocalDateTime createdAt;
}
