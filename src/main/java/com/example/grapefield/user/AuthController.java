package com.example.grapefield.user;

import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  //RTOKEN이 남아있을 경우 ATOKEN 재발급
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    String refreshToken = null;

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("RTOKEN".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("success", false, "message", "Refresh Token is missing."));
    }

    try {
      String newAccessToken = authService.refreshAccessToken(refreshToken);
      ResponseCookie accessTokenCookie = CookieUtil.createAccessTokenCookie(newAccessToken);
      response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

      User user = authService.parseUserFromToken(newAccessToken);
      return ResponseEntity.ok().body(
          Map.of(
              "success", true,
              "userIdx", user.getIdx(),
              "username", user.getUsername(),
              "email", user.getEmail(),
              "role", user.getRole()
          )
      );
    } catch (ExpiredJwtException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("success", false, "message", "Refresh Token expired. Please login again."));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("success", false, "message", "Invalid Refresh Token."));
    }
  }

  @GetMapping("/status")
  public ResponseEntity<?> checkStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.ok(Map.of("authenticated", false, "reason", "no_authentication", "message", "인증 정보가 없습니다."
      ));
    }

    User user = userDetails.getUser();
    return ResponseEntity.ok(Map.of(
        "authenticated", true,
        "userIdx", user.getIdx(),
        "username", user.getUsername(),
        "email", user.getEmail(),
        "role", user.getRole()
    ));
  }
}
