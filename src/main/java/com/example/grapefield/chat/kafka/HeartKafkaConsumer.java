package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.response.HeartResp;
import com.example.grapefield.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartKafkaConsumer {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(
            topicPattern="^chat-like-\\d+$",
            groupId="${spring.kafka.consumer.heart.group-id}",
            containerFactory="heartKafkaListenerContainerFactory"
    )
    public void consumeHeart(ChatHeartKafkaReq chatHeartKafkaReq) {
        Long roomIdx = chatHeartKafkaReq.getRoomIdx();
        String redisKey = "chat:" + roomIdx + ":likes";
        log.info("✅ KafkaConsumer 좋아요 ♥\uFE0F 하트 수신: roomIdx={}", chatHeartKafkaReq.getRoomIdx());

        // Redis INCR
        // 1. Redis 서버에 실시간 heart count 증가
        Long newCount = redisTemplate.opsForValue().increment(redisKey);
        if (newCount == null) {
            // Redis 미초기화 시 1로 설정
            redisTemplate.opsForValue().set(redisKey, 1L);
            newCount = 1L;
        }


        // DataBase
        // 1. DB 하트 수 증가
        chatRoomService.increaseHeartCount(roomIdx);
        // 2. WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        HeartResp resp = new HeartResp(roomIdx, newCount);

        messagingTemplate.convertAndSend("/topic/chat.room.likes." + roomIdx, resp);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", roomIdx);
    }
}
