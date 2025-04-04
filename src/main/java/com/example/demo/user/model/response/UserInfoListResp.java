package com.example.demo.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="회원 목록 정보 응답")
public class UserInfoListResp {
  @Schema(description="실명(문자열)", example = "gildong")
  private String username;
  @Schema(description="이메일(문자열)", example = "example@example.com")
  private String email;
  @Schema(description = "가입일", example = "2025-04-01T14:30:00")
  private LocalDateTime createdAt;
  @Schema(description = "사이트 내 권한", example = "user")
  private String role;

}
