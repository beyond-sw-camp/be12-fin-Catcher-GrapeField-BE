package com.example.grapefield.utils;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

  // 액세스 토큰용 쿠키 생성
  public static ResponseCookie createAccessTokenCookie(String token) {
    return ResponseCookie.from("ATOKEN", token)
        .path("/")
        .httpOnly(false) // 개발 환경 설정
        .secure(false)
        .sameSite("Lax")
        .maxAge(3600)    // 1시간
        .build();
  }

  // 리프레시 토큰용 쿠키 생성
  public static ResponseCookie createRefreshTokenCookie(String token) {
    return ResponseCookie.from("RTOKEN", token)
        .path("/")
        .httpOnly(false)
        .secure(false)
        .sameSite("Lax")
        .maxAge(14 * 24 * 3600) // 2주
        .build();
  }

  // 쿠키 삭제용
  public static ResponseCookie deleteCookie(String name) {
    return ResponseCookie.from(name, "")
        .path("/")
        .maxAge(0)
        .build();
  }
}