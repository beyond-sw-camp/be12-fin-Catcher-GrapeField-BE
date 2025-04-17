package com.example.grapefield.config;

import com.example.grapefield.config.filter.JwtFilter;
import com.example.grapefield.config.filter.LoginFilter;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
  private final AuthenticationConfiguration authConfiguration;
  private final JwtUtil jwtUtil; // JwtUtil 의존성 주입
  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain configureChain(HttpSecurity http) throws Exception {
    // 기본 HTTP 인증과 폼 로그인 비활성화 (JWT 사용)
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);

    // 로그아웃 설정
    http.logout(logout -> logout
        .logoutUrl("/logout")
        .addLogoutHandler((request, response, authentication) -> {
          try {
            // 쿠키에서 사용자 정보 추출
            Cookie[] cookies = request.getCookies();
            Long userIdx = null;

            if (cookies != null) {
              for (Cookie cookie : cookies) {
                if (cookie.getName().equals("ATOKEN")) {
                  // 만료된 토큰에서도 사용자 정보 추출 시도
                  User user = JwtUtil.getUserAllowExpired(cookie.getValue());
                  if (user != null) {
                    userIdx = user.getIdx();
                    break;
                  }
                }
              }
            }

            if (userIdx != null) {
              log.info("로그아웃 - 사용자 ID 추출: {}", userIdx);
              jwtUtil.removeRefreshToken(userIdx);
            } else {
              log.warn("로그아웃 - 사용자 ID 추출 실패");
            }
          } catch (Exception e) {
            log.error("로그아웃 처리 중 오류", e);
          }
        })
        .logoutSuccessHandler((request, response, authentication) -> {
          // Access Token 쿠키 삭제
          ResponseCookie accessTokenCookie = ResponseCookie.from("ATOKEN", "")
              .path("/")
              .httpOnly(true)
              .secure(true)
              .sameSite("Strict")
              .maxAge(0)
              .build();

          // Refresh Token 쿠키도 삭제
          ResponseCookie refreshTokenCookie = ResponseCookie.from("RTOKEN", "")
              .path("/")
              .httpOnly(true)
              .secure(true)
              .sameSite("Strict")
              .maxAge(0)
              .build();

          response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
          response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("{\"message\":\"로그아웃 성공\"}");
        })
    );

    // URL 기반 권한 설정 (기존 코드 유지)
    http.authorizeHttpRequests(authorizeRequests -> {
      authorizeRequests
          .requestMatchers("/user/signup", "/login", "/logout", "/user/email_verify", "/user/email_verify/**", "/events/**", "/participant/**","/post/list/**", "/post/**", "/comment/**", "/review/**").permitAll()
          .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
              "/v3/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()
          .requestMatchers("/admin/**", "/events/register", "/participant/register").hasRole("ADMIN")
          .requestMatchers("/post/register", "/post/update/**", "/post/delete/**",
              "/comment/register", "/comment/update/**", "/comment/delete/**",
              "/user/**", "/review/register", "/review/update", "/review/delete").hasAnyRole("USER", "ADMIN")
          .anyRequest().authenticated();
    });

    // 세션 비활성화 (JWT 사용)
    http.sessionManagement(AbstractHttpConfigurer::disable);

    // 인증 필터 등록
    LoginFilter loginFilter = new LoginFilter(
        authConfiguration.getAuthenticationManager(),
        jwtUtil
    );
    JwtFilter jwtFilter = new JwtFilter(jwtUtil);

    http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}