package com.example.grapefield.config;

import com.example.grapefield.config.filter.JwtFilter;
import com.example.grapefield.config.filter.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
  private final AuthenticationConfiguration authConfiguration;
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
        .logoutUrl("/logout")  // API 경로 형태로 변경
        .deleteCookies("ATOKEN")
          .logoutSuccessHandler((request, response, authentication) -> {
          // 토큰 쿠키 삭제
          ResponseCookie deleteCookie = ResponseCookie.from("ATOKEN", "")
              .path("/")
              .httpOnly(true)
              .secure(true)
              .sameSite("Strict")  // CSRF 추가 보호
              .maxAge(0)           // 즉시 만료
              .build();
          response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

          // JSON 응답 반환
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("{\"message\":\"로그아웃 성공\"}");
        })
    );

    // URL 기반 권한 설정
    http.authorizeHttpRequests(authorizeRequests -> {
      authorizeRequests
          // 인증 없이 접근 가능한 경로
          .requestMatchers("/user/signup", "/login", "/logout", "/user/email_verify", "/user/email_verify/**", "/events/**", "/participant/**","post/list/**").permitAll()
          // Swagger UI 접근 허용
          .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
              "/v3/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()
          // 관리자 권한 필요
          .requestMatchers("/admin/**", "/events/register", "/participant/register").hasRole("ADMIN")
          // 일반 사용자 권한 필요
          .requestMatchers("/post/register", "/post/update/**", "/post/delete/**",
              "/comment/register", "/comment/update/**", "/comment/delete/**",
              "/user/**").hasAnyRole("USER", "ADMIN")

          // 기타 모든 요청은 인증 필요 (가장 일반적인 패턴)
          .anyRequest().authenticated();
    });
    // 세션 비활성화 (JWT 사용)
    http.sessionManagement(AbstractHttpConfigurer::disable);

    // 인증 필터 등록
    http.addFilterAt(new LoginFilter(authConfiguration.getAuthenticationManager()),
        UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}