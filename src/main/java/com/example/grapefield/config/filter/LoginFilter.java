package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserLoginReq;
import com.example.grapefield.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    UsernamePasswordAuthenticationToken token;
    try {
      UserLoginReq user = new ObjectMapper().readValue(request.getInputStream(), UserLoginReq.class);
      token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return authenticationManager.authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.user(); // CustomUserDetails에서 User 객체 가져오기
    String jwt = JwtUtil.generateToken(user.getIdx(), user.getUsername(), user.getEmail(), user.getRole());

    // 쿠키 설정 - 보안 설정 유지
    ResponseCookie cookie = ResponseCookie.from("ATOKEN", jwt)
            .path("/")
            .httpOnly(false)  // .httpOnly(true) //=> JavaScript에서 접근 불가능
            .secure(false)    // ..secure(true) //=> HTTPS에서만 전송
            .sameSite("Lax")  // .sameSite("Strict")  //=> CSRF 보호 강화
            .maxAge(3600)
            .build();
    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    // 토큰 없이 사용자 기본 정보만 응답 본문에 포함
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("userIdx", user.getIdx());
    responseBody.put("username", user.getUsername());
    responseBody.put("email", user.getEmail());
    responseBody.put("role", user.getRole());
    responseBody.put("authenticated", true);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }

  // 로그인 인증 실패 시 처리
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
          throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 기본값 401
    response.setContentType("application/json;charset=UTF-8");
    String message;
    if (failed instanceof DisabledException) {
      message = "이메일 인증이 필요합니다.";
      response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403으로 내려도 괜찮아요
    } else {
      message = "이메일 또는 비밀번호가 잘못되었습니다.";
    }
    response.getWriter().write("{\"message\": \"" + message + "\"}");
  }
}