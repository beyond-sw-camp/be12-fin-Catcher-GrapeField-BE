package com.example.grapefield.user;

import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final JwtUtil jwtUtil;

  public String refreshAccessToken(String refreshToken) {
    return jwtUtil.reissueAccessToken(refreshToken);
  }

  public User parseUserFromToken(String token) {
    return JwtUtil.getUser(token); // 유효한 토큰 기준
  }
}
