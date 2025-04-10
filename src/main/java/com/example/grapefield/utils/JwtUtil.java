package com.example.grapefield.utils;

import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
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

  public static User getUser(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();
      return User.builder()
              .idx(claims.get("userIdx", Long.class))
              .username(claims.get("userUserName", String.class))
              .email(claims.get("userEmail", String.class))
              .role(claims.get("userRole", UserRole.class))
              .build();
    } catch (ExpiredJwtException e) {
      System.out.println("토큰이 만료되었습니다!");
      return null;
    }
  }
  
  public static String generateToken(Long idx, String username, String email, UserRole role) {
    Claims claims = Jwts.claims();
    claims.put("userIdx", idx);
    claims.put("userUserName", username);
    claims.put("userEmail", email);
    claims.put("userRole", role);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public static String refreshToken(String oldToken) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(oldToken)
              .getBody();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    } catch (ExpiredJwtException e) {
      System.out.println("토큰이 만료되었습니다!");
      return null;
    }
  }

  public static boolean validate(String token) {
    try {
      if(token == null) { return false;}
      Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (ExpiredJwtException e) {
      System.out.println("토큰이 만료되었습니다!");
      return false;
    }
    return true;
  }
}
