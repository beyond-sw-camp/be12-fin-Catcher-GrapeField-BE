package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.kafka.ChatKafkaProducer;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageReq;
import com.example.grapefield.chat.service.ChatMessageService;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send.{roomIdx}")
    public void sendMessage(@DestinationVariable Long roomIdx,
                            @Payload ChatMessageReq chatMessageReq,
                            @AuthenticationPrincipal( expression = "user") User user
                            /*, SimpMessageHeaderAccessor headerAccessor */) {
        // 개발 테스트용 로그
        log.info("📡 WebSocket 메시지 수신: roomIdx={}, content={}, username={}",
                roomIdx, chatMessageReq.getContent(), user.getUsername());
        // 1. 라우팅 요청변수 roomIdx와 DTO의 roomIDx 일치여부 검증
        if (!roomIdx.equals(chatMessageReq.getRoomIdx())) {
            throw new IllegalArgumentException("[roomIdx] MessageMapping URL 경로 변수와 메시지 body의 roomIdx 값이 일차하지 않음.");
        }

        // 2. 채팅방이 DB와 Kafka 모두에 존재하는지 보장
        chatRoomService.ensureRoomExists(chatMessageReq.getRoomIdx(), "기본 채팅방");

        // 3. kafka로 메시지 전송
        //  Kafka의 이벤트에 담아서 클라이언트로부터의 메시지를 kafka로 전송
        ChatMessageKafkaReq event = new ChatMessageKafkaReq(
                roomIdx,
                user.getIdx(), // (인증) 사용자 ID를 서버에서 직접 가져오기
                chatMessageReq.getContent()
        );
        chatKafkaProducer.sendMessage(event);

        // 4. 브로커로도 전송 -WebSocket 구독자에게 broadcast
        messagingTemplate.convertAndSend("/topic/chat.room." + roomIdx, event); //KafkaReq를 그대로 전송
    }


    //Swagger 노출용으로, 실제로는 아무 기능이 없는 빈 메소드
    @Operation(summary = "웹소켓 메시지 포맷 예시", description = "웹소켓으로 전송될 메시지 구조를 문서화합니다.")
    @PostMapping("/docs/chat/websocket")
    public ResponseEntity<String> describeWebSocketMessage(
            @RequestBody @Valid ChatMessageReq request) {
        return ResponseEntity.ok("웹소켓 메시지 포맷 확인용 API입니다.");
    }
}

