package com.example.grapefield.config.filter;

import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserLoginReq;
import com.example.grapefield.user.model.request.UserSignupReq;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

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
        User user = (User) auth.getPrincipal();
        String jwt = JwtUtil.generateToken(user.getIdx(), user.getUsername(), user.getEmail(), user.getRole());
        ResponseCookie cookie = ResponseCookie.from("ATOKEN", jwt)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(3600) // 1시간(3600초) 유효시간
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}