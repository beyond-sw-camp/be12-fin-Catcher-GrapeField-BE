package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.chat.service.KafkaTopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartKafkaConsumer {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTopicService kafkaTopicService;
    @KafkaListener(
            topicPattern="^chat-like-\\d+$",
            groupId="chat-heart-group",
            containerFactory="heartKafkaListenerContainerFactory"
    )
    public void consumeHeart(ChatHeartKafkaReq chatHeartKafkaReq) {
        log.info("✅ KafkaConsumer 좋아요 ♥\uFE0F 하트 수신: roomIdx={}", chatHeartKafkaReq.getRoomIdx());

        kafkaTopicService.createHeartKafkaTopicIfNotExist(chatHeartKafkaReq);

        // 1. DB 하트 수 증가
        chatRoomService.increaseHeartCount(chatHeartKafkaReq.getRoomIdx());
        // 2. WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        messagingTemplate.convertAndSend("/topic/chat.room.likes." + chatHeartKafkaReq.getRoomIdx(), chatHeartKafkaReq);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", chatHeartKafkaReq.getRoomIdx());
    }
}
