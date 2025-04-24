package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.kafka.ChatKafkaProducer;
import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
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
// import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "6. 웹소켓 채팅", description = "채팅 전송을 담당하는 웹소켓 채팅 컨트롤러")
@Controller
public class ChatWebSocketController {
    private final ChatKafkaProducer chatKafkaProducer;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send.{roomIdx}")
    @Operation(summary = "채팅 메시지 전송", description = "클라이언트로부터 채팅 메시지를 수신하고, Kafka로 전달")
    public void sendMessage(Principal principal, @Payload ChatMessageReq chatMessageReq) {
        Authentication auth = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        // 개발 테스트용 로그
        log.info("userDetails.getIdx(); : {}", user.getIdx());
        log.info("WebSocket 메시지 수신: roomIdx={}, content={}, principal={}",
                chatMessageReq.getRoomIdx(), chatMessageReq.getContent(), user.getIdx());

        ChatMessageKafkaReq chatMessageKafkaReq =
                new ChatMessageKafkaReq(chatMessageReq.getRoomIdx(), user.getIdx(), chatMessageReq.getContent());

        chatKafkaProducer.sendMessage(chatMessageKafkaReq);//  클라이언트로부터의 메시지를 kafka로 전송
    }

    @MessageMapping("/chat.like.{roomIdx}")
    @Operation(summary = "채팅방 하트 전송", description = "클라이언트에서 전송한 하트(좋아요) 이벤트를 Kafka로 전달")
    public void likeRoom(@DestinationVariable Long roomIdx,
                         @Payload ChatHeartKafkaReq heartReq,
                         Principal principal) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        Long userIdx = userDetails.getUser().getIdx();
        log.info("WebSocket ❤️ 하트 수신: roomIdx={}, userIdx={}", roomIdx, userIdx);
        chatKafkaProducer.likeRoom(heartReq);
    }


    //Swagger 노출용으로, 실제로는 아무 기능이 없는 빈 메소드
    @Operation(summary = "웹소켓 메시지 포맷 예시", description = "웹소켓으로 전송될 메시지 구조를 문서화합니다.")
    @PostMapping("/docs/chat/websocket")
    public ResponseEntity<String> describeWebSocketMessage(
            @RequestBody @Valid ChatMessageReq request) {
        return ResponseEntity.ok("웹소켓 메시지 포맷 확인용 API입니다.");
    }


}

