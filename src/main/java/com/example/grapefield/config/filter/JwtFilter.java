package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
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
          if (cookie.getName().equals("ATOKEN")) {
            jwtToken = cookie.getValue();
          }
          if (cookie.getName().equals("RTOKEN")) {
            refreshToken = cookie.getValue();
          }
        }
      }

      logger.info("쿠키에서 추출한 토큰: {}", (jwtToken != null ? "있음" : "없음"));

      if (jwtToken != null && !jwtToken.isEmpty()) {
        try {
          // 토큰 유효성 검사
          if (!JwtUtil.validate(jwtToken)) {
            logger.warn("만료된 토큰 감지");

            // Refresh Token으로 재발급 시도
            if (refreshToken != null && !refreshToken.isEmpty()) {
              try {
                // Refresh Token으로 새 Access Token 발급
                String newAccessToken = jwtUtil.reissueAccessToken(refreshToken);

                // 새 Access Token을 쿠키에 설정
                ResponseCookie newAccessTokenCookie = ResponseCookie.from("ATOKEN", newAccessToken)
                    .path("/")
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax")
                    .maxAge(3600) // 1시간
                    .build();

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
            } else {
              // Refresh Token 없음
              logger.warn("Refresh Token 없음");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"message\": \"Refresh Token is missing\"}");
              SecurityContextHolder.clearContext();
              return;
            }
          } else {
            // 유효한 토큰 처리
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
          }
        } catch (Exception e) {
          logger.error("토큰 처리 중 오류: {}", e.getMessage());
          SecurityContextHolder.clearContext();
        }
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      logger.error("JWT 필터 처리 중 전역 오류 발생: {}", e.getMessage());
      filterChain.doFilter(request, response);
    }
  }
}