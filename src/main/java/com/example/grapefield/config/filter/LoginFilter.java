package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserLoginReq;
import com.example.grapefield.utils.CookieUtil;
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
  private final JwtUtil jwtUtil;

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
    User user = userDetails.getUser();

    // Access Token 생성
    String accessToken = JwtUtil.generateAccessToken(
        user.getIdx(),
        user.getUsername(),
        user.getEmail(),
        user.getRole()
    );

    // Refresh Token 생성
    String refreshToken = jwtUtil.generateRefreshToken(user.getIdx());

    // Access Token 쿠키 설정
    ResponseCookie accessTokenCookie = CookieUtil.createAccessTokenCookie(accessToken);
    // Refresh Token 쿠키 설정
    ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(refreshToken);

    response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    // 사용자 정보 응답
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