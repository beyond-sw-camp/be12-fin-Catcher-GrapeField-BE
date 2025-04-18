package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.CookieUtil;
import com.example.grapefield.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String requestURI = request.getRequestURI();
      logger.info("JWT 필터 처리 중: {}", requestURI);

      Cookie[] cookies = request.getCookies();
      String jwtToken = null;
      String refreshToken = null;

      // 쿠키에서 Access Token과 Refresh Token 추출
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("ATOKEN")) {jwtToken = cookie.getValue();}
          if (cookie.getName().equals("RTOKEN")) {refreshToken = cookie.getValue();}
        }
      }

      logger.info("ATOKEN: {}, RTOKEN: {}", (jwtToken != null ? "있음" : "없음"), (refreshToken != null ? "있음" : "없음"));

      // ATOKEN이 없거나 유효하지 않고, RTOKEN이 있는 경우
      if ((jwtToken == null || jwtToken.isEmpty() || !JwtUtil.validate(jwtToken))
          && refreshToken != null && !refreshToken.isEmpty()) {
        try {
          // Refresh Token으로 새 Access Token 발급
          logger.info("Refresh Token으로 새 Access Token 발급 시도");
          String newAccessToken = jwtUtil.reissueAccessToken(refreshToken);

          // 새 Access Token을 쿠키에 설정
          ResponseCookie newAccessTokenCookie = CookieUtil.createAccessTokenCookie(newAccessToken);
          response.setHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

          // 새 토큰으로 사용자 인증
          User user = JwtUtil.getUser(newAccessToken);
          if (user != null) {
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    customUserDetails.getAuthorities()
                );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("토큰 재발급 및 사용자 인증 성공: {}", user.getUsername());
          } else {
            logger.warn("토큰 재발급 후 사용자 정보 추출 실패");
            SecurityContextHolder.clearContext();
          }
        } catch (Exception e) {
          logger.error("토큰 재발급 실패: {}", e.getMessage());
          // 재발급 실패 시 401 Unauthorized 응답
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json;charset=UTF-8");
          response.getWriter().write("{\"message\": \"Token reissue failed\"}");
          SecurityContextHolder.clearContext();
          return;
        }
      } else if (jwtToken != null && !jwtToken.isEmpty() && JwtUtil.validate(jwtToken)) {
        // 유효한 ATOKEN이 있는 경우 처리
        User user = JwtUtil.getUser(jwtToken);
        if (user != null) {
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
          logger.warn("사용자 정보 추출 실패");
          SecurityContextHolder.clearContext();
        }
      } else if (refreshToken == null || refreshToken.isEmpty()) {
        // ATOKEN도 없고 RTOKEN도 없는 경우
        logger.warn("인증 토큰 없음");
        SecurityContextHolder.clearContext();
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      logger.error("JWT 필터 처리 중 전역 오류 발생: {}", e.getMessage());
      filterChain.doFilter(request, response);
    }
  }
}