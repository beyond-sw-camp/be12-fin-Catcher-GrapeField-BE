package com.example.grapefield.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration // 스프링 설정 클래스로 등록해주기
@EnableWebSocketMessageBroker //웹소켓 메시지 브로커 활성화. "STOMP 기반 메시징을 사용하겠다"
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // 인터페이스의 추상메서드 구현
        registry.addEndpoint("/ws") // 클라이언트에서 연결할 엔드포인트 URL 지정
                .setAllowedOriginPatterns("*") // CORS 설정: 모든 출처origin에서 접근 허용
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(new JwtHandshakeInterceptor())
                .withSockJS(); // WebSocket이 지원되지 않는 브라우저에서 자동으로 SockJS fallback (=HTTP기반 통신) 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 인터페이스의 추상메서드 구현
        /* 해설
        "/topic" prefix
        [서버] ---- > [클라이언트]
        : 메시지 broadcast

        "/app" prefix
        [클라이언트] ---- > [서버]
        : 서버로 메세지 전송
        예: /app/chat.send → @MessageMapping("/chat.send")으로 라우팅됨
        */
        registry.enableSimpleBroker("/topic"); // 구독용 메시지 브로커 활성화. 브로커가 발행publish할 경로 prefix, 클라이언트가 구독subscribe할 때 사용
        registry.setApplicationDestinationPrefixes("/app");
    }
}
