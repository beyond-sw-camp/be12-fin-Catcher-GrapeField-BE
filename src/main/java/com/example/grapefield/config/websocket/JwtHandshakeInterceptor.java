package com.example.grapefield.config.websocket;

import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (!(request instanceof ServletServerHttpRequest)) {
            logger.warn("🔴 핸드셰이크 요청 오류: Handshake not from HTTP requeest");
            return false;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        // 쿠키에서 ATOKEN 뽑아내기
        String jwt = null;
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if("ATOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.info("✅ 쿠키에 ATOKEN값이 존재");
                    break;
                }
            }
        }

        // jwt 유효성 검사
        if (jwt != null && JwtUtil.validate(jwt)) {
            User user = JwtUtil.getUser(jwt);
            if (user == null) {
                logger.warn("🔴 JWT (ATOKEN)에서 사용자 정보 추출 실패");
                return false;
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            attributes.put("userIdx", user.getIdx()); // 웹소켓 세션에 식별자 저장
            logger.info("✅ WebSocket 인증 성공: userIdx={}, principal={}", user.getIdx(), SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            return true;
        }
        logger.warn("🔴 WebSocket 인증 실패: JWT가 없거나 유효하지 않음.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 공백
    }
}
