package com.example.grapefield.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;


// 웹소켓 경로별 인가 설정
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher().permitAll() // 목적지가 null이면 허용 (초기 메세지)
                .simpDestMatchers("/app/**").authenticated() // 인증된 사용자만 (=client=브라우저)서버로 메세지 전송 가능
                .simpSubscribeDestMatchers("/topic/**").authenticated()  //인증된 사용자만 서버로부터 메세지 구독 가능
                .anyMessage().denyAll(); // 그 외 메세지는 다 거부
    }
    @Override
    protected boolean sameOriginDisabled() {
        return true; // CORS 무시 // 개발용
    }
}
