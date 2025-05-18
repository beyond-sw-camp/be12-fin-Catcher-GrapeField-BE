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
import java.time.Duration;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartKafkaConsumer {
    // 🌟 Redis Sorted Set으로 최근 인기 집계, 전체 누적 집계를 처리한다...
    private static final String HOT_SET_KEY = "hot:hearts"; // ⭐ Redis ZSet 키: 최근 시간창 내 하트 증가량을 저장
    private static final long WINDOW_SECONDS = 1800; // ⭐ 30분 // 윈도우 크기(초 단위):
    private final RedisTemplate<String, Object> redisTemplate; // 🌟 Redis로 실시간 하트 반환한다...

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

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
        Long newHeartCount;
        if(added != null || added == 0) {
            newHeartCount = chatRoomService.increaseHeart(roomIdx);
            // ⭐ 전체 누적 순위용 ZSet 갱신
            redisTemplate.opsForZSet()
                    .incrementScore("leaderboard:hearts",roomIdx.toString(), 1.0);
            // ⭐ 최근 윈도우 순위용 ZSet 갱신: period WINDOW_SECONDS 내 집계
            Double newScore = redisTemplate.opsForZSet()
                    .incrementScore(HOT_SET_KEY, roomIdx.toString(), 1.0);
            // ⭐  TTL 설정: 키 생성 시에만 expire 호출
            Long ttl = redisTemplate.getExpire(HOT_SET_KEY);
            if (ttl < 0) {
                // 키가 없거나 만료 설정이 없으면 WINDOW_SECONDS로 설정
                redisTemplate.expire(HOT_SET_KEY, Duration.ofSeconds(WINDOW_SECONDS));
            }

        } else {
            log.info("이미 처리된 요청이므로 취소");
            String redisKey = "chat:"+roomIdx+":likes";
            newHeartCount = Long.parseLong((redisTemplate.opsForValue().get(redisKey)).toString());
        }

        //


        // WebSocket 브로커로 브로드캐스트 (프론트에서 애니메이션 띄우게)
        HeartResp resp = new HeartResp(roomIdx, newHeartCount);
        // // Redis 없는 환경에서는 다음으로 바꾸면 된다.⤵️⤵️
        // // 그리고.. public Long increaseHeart(Long roomIdx)레디스 없이 저장로직을 수정해야 한다...
        // HeartResp resp = new HeartResp(roomIdx, chatRoom.getHeartCnt());
        messagingTemplate.convertAndSend("/topic/chat.room.likes." + roomIdx, resp);
        log.info("✅ 📡 WebSocket Broadcast 좋아요 ♥\uFE0F 하트 -> roomIdx={}", roomIdx);
    }
}
