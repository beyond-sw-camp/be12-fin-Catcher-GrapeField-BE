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
        String heartIdx = chatHeartKafkaReq.getHeartIdx();
        log.info("✅ KafkaConsumer 좋아요 ♥\uFE0F 하트 수신: roomIdx={}, heartIdx={}", roomIdx, heartIdx);
        // 기존에 없는 경우 추가 되지 않기 때문에 때문에 added에 0을 반환한다.
        Long added = redisTemplate.opsForSet().add("processed:hearts", heartIdx);
        Long newCount;
        if(added != null && added > 0) {
            newCount=chatRoomService.increaseHeart(roomIdx);
        } else {
            log.info("이미 처리된 요청이므로 취소");
            String redisKey = "chat:"+roomIdx+":likes";
            newCount = Long.parseLong((redisTemplate.opsForValue().get(redisKey)).toString());
        }
        // 2. WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        HeartResp resp = new HeartResp(roomIdx, newCount);
        // // Redis 없는 환경에서는 다음으로 바꾸면 된다.⤵️⤵️
        // HeartResp resp = new HeartResp(roomIdx, chatRoom.getHeartCnt());
        messagingTemplate.convertAndSend("/topic/chat.room.likes." + roomIdx, resp);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", roomIdx);
    }
}
