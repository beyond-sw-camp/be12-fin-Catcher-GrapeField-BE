package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.response.ChatParticipantEventResp;
import com.example.grapefield.chat.model.response.UserChatListEventResp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference; // 이 import가 누락되었을 수 있음
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatParticipantEventKafkaConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    // ObjectMapper 제거 - 더 이상 필요 없음!

    // 참여자 수 변경 이벤트 처리
    @KafkaListener(
            topics = "chat-participant-events",
            groupId = "chat-participant-group",
            containerFactory = "participantEventKafkaListenerContainerFactory"
    )
    public void handleParticipantEvent(ChatParticipantEventResp event) {
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

    // 사용자 채팅방 리스트 이벤트 처리
    @KafkaListener(
            topics = "user-chatlist-events",
            groupId = "user-event-group",
            containerFactory = "userEventKafkaListenerContainerFactory"
    )
    public void handleUserChatListEvent(UserChatListEventResp event) {
        try {
            if ("USER_CHATLIST_UPDATE".equals(event.getType())) {
                String userTopic = "/topic/user/" + event.getUserIdx() + "/chatlist";

                Map<String, Object> notifyMessage = Map.of(
                        "action", event.getAction(),
                        "roomIdx", event.getRoomIdx(),
                        "timestamp", System.currentTimeMillis()
                );

                messagingTemplate.convertAndSend(userTopic, notifyMessage);
                log.info("📢 [사용자 채팅방 리스트] 웹소켓 전송: {} → {}", userTopic, notifyMessage);
            }
        } catch (Exception e) {
            log.error("❌ 사용자 채팅방 리스트 이벤트 처리 실패: ", e);
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