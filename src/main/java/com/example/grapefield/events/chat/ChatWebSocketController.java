package com.example.grapefield.events.chat;

import com.example.grapefield.events.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.events.chat.model.request.ChatMessageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "6. 웹소켓 채팅", description = "채팅 전송을 담당하는 웹소켓 채팅 컨트롤러")
@Controller
public class ChatWebSocketController {
    private final KafkaTemplate<String, ChatMessageKafkaReq> kafkaTemplate;

    @MessageMapping("/chat/{roomIdx}")
    public void sendMessage( @DestinationVariable Long roomIdx, ChatMessageReq message) {
        // Kafka로 메시지 전송 (DB에 저장 X)
        ChatMessageKafkaReq event = new ChatMessageKafkaReq(roomIdx, message.getSendUserIdx(), message.getContent());
        kafkaTemplate.send("chat-message-topic", event);
    }

    //Swagger 노출용으로, 실제로는 아무 기능이 없는 빈 메소드
    @Operation(summary = "웹소켓 메시지 포맷 예시", description = "웹소켓으로 전송될 메시지 구조를 문서화합니다.")
    @PostMapping("/docs/chat/websocket")
    public ResponseEntity<String> describeWebSocketMessage(
            @RequestBody @Valid ChatMessageReq request) {
        return ResponseEntity.ok("웹소켓 메시지 포맷 확인용 API입니다.");
    }
}

