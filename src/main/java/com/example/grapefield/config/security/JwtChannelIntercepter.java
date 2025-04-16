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

    private static final String COOKIE_HEADER = "cookie";
    private static final String TOKEN_NAME = "ATOKEN";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("MessageHeaderAccessor.getAccessor(message,... 의 message:"+ message.getPayload().toString());
        log.info("StompHeaderAccessor.class: " + StompHeaderAccessor.class.toString());
        log.info("StompHeader Accessor accessor: " + accessor.toString());
        if (accessor == null ) return message;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String cookieHeader = accessor.getFirstNativeHeader(COOKIE_HEADER);
            log.info("cookieHeader: " + cookieHeader);
            if (cookieHeader == null || !cookieHeader.contains(TOKEN_NAME + "=")) {
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
            if (!(authentication.getPrincipal() instanceof CustomUserDetails cud)) {
                log.error("⚠️ Principal이 CustomUserDetails가 아닙니다: {}", authentication.getPrincipal().getClass());
            } else if (cud.user() == null) {
                log.error("⚠️ CustomUserDetails 내부 User 객체가 null입니다!");
            }

            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[WebSocket 인증 완료] 사용자: {} (ID: {})", user.getUsername(), user.getIdx());
        }

        return message;
    }
}
