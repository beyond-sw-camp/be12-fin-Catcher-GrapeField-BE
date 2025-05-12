package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.response.ChatParticipantEventResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatParticipantService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public void handleParticipantChange(ChatParticipantEventResp event) {
        try {
            if ("PARTICIPANT_CHANGE".equals(event.getType())) {
                int count = getParticipantCount(event.getRoomIdx());
                String topic = "/topic/chat/room/" + event.getRoomIdx() + "/participants";
                messagingTemplate.convertAndSend(topic, count);
                log.info("📢 [카프카] {} 브로드캐스트: {} → {}", event.getAction(), topic, count);
            }
        } catch (Exception e) {
            log.error("❌ 참여자 수 카프카 이벤트 처리 실패: ", e);
        }
    }

    private int getParticipantCount(Long roomIdx) {
        String key = "chat:room:" + roomIdx + ":participants";
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Integer.parseInt(cached);
        }
        return 0;
    }
}