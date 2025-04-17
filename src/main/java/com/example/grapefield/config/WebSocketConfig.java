package com.example.grapefield.config;

import com.example.grapefield.config.security.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

@Configuration // 스프링 설정 클래스로 등록해주기
@EnableWebSocketMessageBroker //웹소켓 메시지 브로커 활성화. "STOMP 기반 메시징을 사용하겠다"
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) { // 인터페이스의 추상메서드 구현
//        registry.addEndpoint("/ws") // 클라이언트에서 연결할 엔드포인트 URL 지정
//                .setAllowedOriginPatterns("*") // CORS 설정: 모든 출처origin에서 접근 허용
//                .withSockJS(); // WebSocket이 지원되지 않는 브라우저에서 자동으로 SockJS fallback (=HTTP기반 통신) 허용
//    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // 인터페이스의 추상메서드 구현
        registry.addEndpoint("/ws") // 클라이언트에서 연결할 엔드포인트 URL 지정
                .setAllowedOriginPatterns("http://localhost:5173") // CORS 설정: 모든 출처origin에서 접근 허용하면서 .setSessionCookieNeeded(true) 면 오류남!
                .withSockJS() // WebSocket이 지원되지 않는 브라우저에서 자동으로 SockJS fallback (=HTTP기반 통신) 허용
                .setSessionCookieNeeded(true); // 매우 중요: SockJS 세션에서 쿠키 받아올 때 필요함
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


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                securityContextChannelInterceptor(), // 인증 context 설정
                jwtChannelInterceptor); //JWT 인증 수행
    }

    @Bean
    public SecurityContextChannelInterceptor securityContextChannelInterceptor() {
        return new SecurityContextChannelInterceptor();
    }
}
