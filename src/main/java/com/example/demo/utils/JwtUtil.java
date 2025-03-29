package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwtData.secret}")
    private static String SECRET;
    @Value("${jwtData.expiration}")
    private static Long EXPIRATION;
    static SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(Long idx, String nickName) {
        Claims claims = Jwts.claims();
        claims.put("idx", idx);
        claims.put("nickName", nickName);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
