package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.response.UserChatListEventResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserChatListService {

    private final SimpMessagingTemplate messagingTemplate;

    public void handleChatListUpdate(UserChatListEventResp event) {
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
}