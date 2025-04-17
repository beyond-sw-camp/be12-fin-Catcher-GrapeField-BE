package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.kafka.ChatKafkaProducer;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.service.ChatMessageService;
import com.example.grapefield.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/*
 * // (STOMP 기반 라우팅으로) 수정 전 코드
 * import org.springframework.kafka.core.KafkaTemplate;
 * */

@Slf4j
@RequiredArgsConstructor
@Tag(name = "6. 웹소켓 채팅", description = "채팅 전송을 담당하는 웹소켓 채팅 컨트롤러")
@Controller
public class ChatWebSocketController {
    /*
    // (STOMP 기반 라우팅으로) 수정 전 코드
    private final KafkaTemplate<String, ChatMessageKafkaReq> kafkaTemplate;

    private final ChatKafkaProducer chatKafkaProducer;
    @MessageMapping("/chat/{roomIdx}")
    public void sendMessage( @DestinationVariable Long roomIdx, ChatMessageReq message) {
        // Kafka로 메시지 전송 (DB에 저장 X)
        ChatMessageKafkaReq event = new ChatMessageKafkaReq(roomIdx, message.getSendUserIdx(), message.getContent());
        kafkaTemplate.send("chat-message-topic", event);
    }
    */
    private final ChatKafkaProducer chatKafkaProducer;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send.{roomIdx}")
    public void sendMessage(@Payload ChatMessageKafkaReq chatMessageKafkaReq,
                            SimpMessageHeaderAccessor headerAccessor) {
        // 개발 테스트용 로그
        log.info("WebSocket 메시지 수신: roomIdx={}, content={}",
                chatMessageKafkaReq.getRoomIdx(), chatMessageKafkaReq.getContent());

        // ✅ 1. 채팅방이 DB와 Kafka 모두 존재하는지 보장
        chatRoomService.ensureRoomExists(chatMessageKafkaReq.getRoomIdx(), "기본 채팅방");

        // 1. kafka로 메시지 전송

        ChatMessageResp resp = chatMessageService.saveMessage(chatMessageKafkaReq);
        //  클라이언트로부터의 메시지를 kafka로 전송
        chatKafkaProducer.sendMessage(resp);


        // 2. WebSocket 브로커로도 전송
        messagingTemplate.convertAndSend("/topic/chat.room." + chatMessageKafkaReq.getRoomIdx(),chatMessageKafkaReq);
    }




    //Swagger 노출용으로, 실제로는 아무 기능이 없는 빈 메소드
    @Operation(summary = "웹소켓 메시지 포맷 예시", description = "웹소켓으로 전송될 메시지 구조를 문서화합니다.")
    @PostMapping("/docs/chat/websocket")
    public ResponseEntity<String> describeWebSocketMessage(
            @RequestBody @Valid ChatMessageReq request) {
        return ResponseEntity.ok("웹소켓 메시지 포맷 확인용 API입니다.");
    }
}

