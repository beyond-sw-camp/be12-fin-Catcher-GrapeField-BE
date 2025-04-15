package com.example.grapefield.config.security;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class JwtChannelIntercepter implements ChannelInterceptor {
    /*
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        Optional<String> jwtTokenOptional = Optional.ofNullable(accessor.getFirstNativeHeader(JWT_TOKEN_HEADER));
        // STOMP 연결 시 Authorization: Bearer ... 헤더에서 JWT 파싱
        String jwtToken = jwtTokenOptional
                .filter(header -> header.startsWith(JWT_TOKEN_PREFIX))
                .map(header ->header.substring(JWT_TOKEN_PREFIX.length()))
                .filter(JwtUtil::validate) //유효성 검증
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing JWT token in WebSocket CONNECT"));
        // 파싱한 JWT 로 user 객체 생성
        User user = JwtUtil.getUser(jwtToken); // User 객체 추출
        if (user == null) {
            throw new IllegalArgumentException("JWT에서 사용자 정보를 추출할 수 없음.");
        }
        CustomUserDetails userDetails = new CustomUserDetails(user); //CustomUserDetails로 Wrapping 후 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // WebSocket 세션에서 사용자 정보 설저어 (STOMP)
        accessor.setUser(authenticationToken); //@AuthenticationPrincipal 사용 가능
        // Spring Security 컨텍스트에도 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Spring Security 전체와 통합
        log.info("[WebSocket 연결 - 인증 완료] 사용자: {}", user.getUsername());
        return message;
    }
    */
    private static final String COOKIE_HEADER = "cookie";
    private static final String TOKEN_NAME = "ATOKEN";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        String cookieHeader = accessor.getFirstNativeHeader(COOKIE_HEADER);
        if (cookieHeader != null || !cookieHeader.isEmpty() || !cookieHeader.contains(TOKEN_NAME + "=")) {
            log.warn("[WebSocket - 인증 실패] 쿠키에 ATOKEN 없음");
            return message;
        }
        String token = Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .filter(c -> c.startsWith(TOKEN_NAME + "="))
                .map(c -> c.substring((TOKEN_NAME + "=").length()))
                .findFirst()
                .orElse(null);
        if (!JwtUtil.validate(token)) {
            log.warn("[WebSocket - 인증 실패] JWT 유효성 검사 실패");
            return message;
        }
        User user = JwtUtil.getUser(token);
        if (user == null) {
            log.warn("[WebSocket - 인증 실패] JWT 토큰 에서 사용자 정보 추출 불가능");
            return message;
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        accessor.setUser(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("[WebSocket 인증 완료] 사용자: {} (ID: {})", user.getUsername(), user.getIdx());
        return message;
    }
}
