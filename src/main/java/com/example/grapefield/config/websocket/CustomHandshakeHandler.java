package com.example.grapefield.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 인증정보가 없으면 연결 차단(또는 익명 처리)
        }
        // UsernamePasswordAuthenticationToken 의 Principal(=CustomUserDetails)을 그대로 반환
        return authentication; // ★ STOMP 메세지 엔드포인트에서 Principal 파라미터로 해당 Authenticaiton 객체가 주입된다!!!
    }
}
