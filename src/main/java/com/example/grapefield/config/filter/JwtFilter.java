package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      // 현재 요청 URI 로깅
      String requestURI = request.getRequestURI();
      logger.info("JWT 필터 처리 중: {}", requestURI);

      Cookie[] cookies = request.getCookies();

      String jwtToken = null;
      if(cookies != null) {
        for(Cookie cookie : cookies) {
          if(cookie.getName().equals("ATOKEN")) {
            jwtToken = cookie.getValue();
            break;
          }
        }
      }

      logger.info("쿠키에서 추출한 토큰: {}", (jwtToken != null ? "있음" : "없음"));

      // 토큰이 있는 경우에만 인증 처리를 시도
      if(jwtToken != null && !jwtToken.isEmpty()) {
        try {
          User user = JwtUtil.getUser(jwtToken);
          logger.info("토큰에서 추출한 사용자: {}", (user != null ? user.getUsername() + ", 역할: " + user.getRole() : "없음"));

          if(user != null) {
            // User 대신 CustomUserDetails 사용
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    customUserDetails.getAuthorities()
                );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("사용자 인증 성공: {}", user.getUsername());
          } else {
            logger.warn("유효한 토큰이지만 사용자 정보를 추출할 수 없음");
          }
        } catch (Exception e) {
          logger.error("토큰 검증 실패: {}", e.getMessage());
          // 토큰 검증 실패시 SecurityContext는 그대로 두고 다음 필터로 진행
        }
      } else {
        logger.debug("JWT 토큰이 없어 인증 없이 진행");
        // 토큰이 없는 경우에는 인증 시도를 하지 않고 그대로 진행
        // SecurityContext는 비어있는 상태로 유지됨
      }

      // 어떤 경우든 필터 체인 계속 진행
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      logger.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage());
      // 오류가 발생해도 요청은 계속 진행
      filterChain.doFilter(request, response);
    }
  }
}