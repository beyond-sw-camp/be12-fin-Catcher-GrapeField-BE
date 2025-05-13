package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.response.HeartResp;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartKafkaConsumer {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @KafkaListener(
            topicPattern="^chat-like-\\d+$",
            groupId="${spring.kafka.consumer.heart.group-id}",
            containerFactory="heartKafkaListenerContainerFactory"
    )
    public void consumeHeart(ChatHeartKafkaReq chatHeartKafkaReq) {
        Long roomIdx = chatHeartKafkaReq.getRoomIdx();
        log.info("✅ KafkaConsumer 좋아요 ♥\uFE0F 하트 수신: roomIdx={}", roomIdx);

        ChatRoom room = chatRoomRepository.findById(roomIdx).orElse(null);
        chatRoomService.increaseHeartCount(roomIdx);
        String redisKey = "chat:" + roomIdx + ":likes";
        redisTemplate.opsForValue().increment(redisKey);
        // Redis INCR
        // 1. Redis 서버에 실시간 heart count 증가
        Long newCount = Objects.requireNonNull(room).getHeartCnt();
        // 2. WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        HeartResp resp = new HeartResp(roomIdx, newCount);
        messagingTemplate.convertAndSend("/topic/chat.room.likes." + roomIdx, resp);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", roomIdx);
    }
}
