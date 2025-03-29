package com.example.demo.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            doFilter(request, response, filterChain);
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ATOKEN")) {
                token = cookie.getValue();
                break;
            }
        }
//        if (token != null) {
//            User user = JwtUtil.buildUserDataFromToken(token);
//            if (user != null) {
//                UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                identityToken.setDetails(user);
//                SecurityContextHolder.getContext().setAuthentication(identityToken);
//            }
//        }
        filterChain.doFilter(request, response);
    }
}
