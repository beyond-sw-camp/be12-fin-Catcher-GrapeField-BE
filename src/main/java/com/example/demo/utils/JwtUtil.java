package com.example.demo.utils;

import com.example.demo.user.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
@RequiredArgsConstructor
public class JwtUtil {

  private static String secret;
  private static long expiration;
  private static SecretKey key;

  @Value("${jwtData.secret}")
  public void setSecret(String secret) {
    JwtUtil.secret = secret;
    JwtUtil.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Value("${jwtData.expiration}")
  public void setExpiration(long expiration) {
    JwtUtil.expiration = expiration;
  }

  public static String generateToken(Long idx, String email, String role) {
    Claims claims = Jwts.claims();
    claims.put("userIdx", idx);
    claims.put("userEmail", email);
    claims.put("userRole", role);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public static User buildUserDataFromToken(String token) {
    try {
      Claims claim = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      return User.builder()
          .idx(claim.get("userIdx", Long.class))
          .email(claim.get("userEmail", String.class))
          .role(claim.get("userRole", String.class))
          .build();
    } catch (Exception e) {
      return null;
    }
  }
}
