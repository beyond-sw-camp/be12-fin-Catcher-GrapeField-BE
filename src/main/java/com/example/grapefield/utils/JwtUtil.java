package com.example.grapefield.utils;

import com.example.grapefield.config.filter.JwtFilter;
import com.example.grapefield.user.UserService;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import com.example.grapefield.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {
  private static String secret;
  private static long accessTokenExpiration;
  private static long refreshTokenExpiration;
  private static SecretKey key;

  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

  @Value("${jwtData.secret}")
  public void setSecret(String secret) {
    JwtUtil.secret = secret;
    JwtUtil.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Value("${jwtData.access-expiration}")
  public void setAccessTokenExpiration(long expiration) {
    JwtUtil.accessTokenExpiration = expiration;
  }

  @Value("${jwtData.refresh-expiration}")
  public void setRefreshTokenExpiration(long expiration) {
    JwtUtil.refreshTokenExpiration = expiration;
  }

  // Access Token 생성
  public static String generateAccessToken(Long idx, String username, String email, UserRole role) {
    Claims claims = Jwts.claims();
    claims.put("userIdx", idx);
    claims.put("userUserName", username);
    claims.put("userEmail", email);
    claims.put("userRole", role.name());

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(Long userIdx) {
    String refreshToken = Jwts.builder()
        .setSubject(String.valueOf(userIdx))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    // Redis에 Refresh Token 저장
    saveRefreshToken(userIdx, refreshToken);

    return refreshToken;
  }

  // Refresh Token Redis에 저장
  private void saveRefreshToken(Long userIdx, String refreshToken) {
    // StringRedisSerializer 사용
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());

    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(
        "refresh_token:" + userIdx,
        refreshToken,
        refreshTokenExpiration,
        TimeUnit.MILLISECONDS
    );
  }

  // Refresh Token 검증 및 재발급
  public String reissueAccessToken(String refreshToken) {
    try {
      // Refresh Token 검증
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(refreshToken)
          .getBody();

      Long userIdx = Long.parseLong(claims.getSubject());

      // Redis에 저장된 Refresh Token과 일치하는지 확인
      ValueOperations<String, String> ops = redisTemplate.opsForValue();

      // 키 직렬화 설정
      redisTemplate.setKeySerializer(new StringRedisSerializer());
      redisTemplate.setValueSerializer(new StringRedisSerializer());

      String storedRefreshToken = ops.get("refresh_token:" + userIdx);

      if (storedRefreshToken == null) {
        throw new RuntimeException("No stored Refresh Token found");
      }

      if (!storedRefreshToken.equals(refreshToken)) {
        throw new RuntimeException("Invalid Refresh Token");
      }

      // 사용자 정보 조회
      User user = getUserByIdx(userIdx);

      // 새로운 Access Token 발급
      return generateAccessToken(
          user.getIdx(),
          user.getUsername(),
          user.getEmail(),
          user.getRole()
      );

    } catch (ExpiredJwtException e) {
      // Refresh Token 만료 시 명확한 예외 처리
      throw new RuntimeException("Refresh Token Expired", e);
    }
  }

  // 로그아웃 시 Refresh Token 제거
  public void removeRefreshToken(Long userIdx) {
    try {
      String key = "refresh_token:" + userIdx;
      Boolean deleted = redisTemplate.delete(key);

      if (Boolean.TRUE.equals(deleted)) {
        log.info("Refresh Token 성공적으로 삭제: {}", userIdx);
      } else {
        log.warn("삭제할 Refresh Token 없음: {}", userIdx);
      }
    } catch (Exception e) {
      log.error("Refresh Token 삭제 중 오류 발생: {}", userIdx, e);
      throw new RuntimeException("Refresh Token 삭제 실패", e);
    }
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
          .role(UserRole.valueOf(claims.get("userRole", String.class)))
          .build();
    } catch (ExpiredJwtException e) {
      // 만료된 토큰에 대해 null 대신 예외 처리
      throw e;
    } catch (Exception e) {
      // 다른 토큰 관련 예외도 null 반환
      return null;
    }
  }

  public static User getUserAllowExpired(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
      return parseUserFromClaims(claims);
    } catch (ExpiredJwtException e) {
      // 만료된 토큰에서도 Claims 추출 가능
      return parseUserFromClaims(e.getClaims());
    } catch (Exception e) {
      return null;
    }
  }

  private static User parseUserFromClaims(Claims claims) {
    return User.builder()
        .idx(claims.get("userIdx", Long.class))
        .username(claims.get("userUserName", String.class))
        .email(claims.get("userEmail", String.class))
        .role(UserRole.valueOf(claims.get("userRole", String.class)))
        .build();
  }


  public static boolean validate(String token) {
    try {
      if (token == null) {
        return false;
      }

      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      return true;
    } catch (ExpiredJwtException e) {
      // 만료된 토큰은 false 반환
      return false;
    } catch (Exception e) {
      // 다른 토큰 관련 예외도 false 처리
      return false;
    }
  }

  // 사용자 정보 조회 메서드
  private User getUserByIdx(Long userIdx) {
    return userRepository.findById(userIdx).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userIdx));
  }
}
