package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.service.ChatRoomService;
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
    private final AdminClient adminClient;
    @KafkaListener(
            topicPattern="^chat-like-\\d+$",
            groupId="chat-heart-group",
            containerFactory="heartKafkaListenerContainerFactory"
    )
    public void consumeHeart(ChatHeartKafkaReq chatHeartKafkaReq) {
        log.info("✅ KafkaConsumer 좋아요 ♥\uFE0F 하트 수신: roomIdx={}", chatHeartKafkaReq.getRoomIdx());
        String topicName = "chat-like-" + chatHeartKafkaReq.getRoomIdx();
        try {
            var existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(topicName)) {
                adminClient.createTopics(List.of(new NewTopic(topicName, 1, (short) 1)));
                log.info("✅ Kafka 토픽 생성 완료: {}", topicName);
            } else {
                log.info("ℹ️ Kafka 토픽 이미 존재: {}", topicName);
            }
        } catch (Exception e) {
            log.warn("⚠️ Kafka 토픽 생성 중 에러: {}", e.getMessage());
        }

        // 1. DB 하트 수 증가
        chatRoomService.increaseHeartCount(chatHeartKafkaReq.getRoomIdx());
        // 2. WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        messagingTemplate.convertAndSend("/topic/chat.room.likes." + chatHeartKafkaReq.getRoomIdx(), chatHeartKafkaReq);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", chatHeartKafkaReq.getRoomIdx());
    }
}
