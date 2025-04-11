package com.example.grapefield.config.filter;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();

    String jwtToken = null;
    if(cookies != null) {
      for(Cookie cookie : cookies) {
        if(cookie.getName().equals("ATOKEN")) {
          jwtToken = cookie.getValue();
        }
      }
    }
    if(jwtToken != null && !jwtToken.isEmpty()) {
      User user = JwtUtil.getUser(jwtToken);
      if(user != null) {
        // User 대신 CustomUserDetails 사용
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
                customUserDetails,  // principal로 CustomUserDetails 사용
                null,
                customUserDetails.getAuthorities()  // User가 아닌 CustomUserDetails에서 권한 가져오기
            );
        // details 필드는 필요시 설정
        // usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}