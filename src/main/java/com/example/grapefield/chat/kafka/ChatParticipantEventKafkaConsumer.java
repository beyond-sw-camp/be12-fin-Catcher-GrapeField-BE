package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.response.ChatParticipantEventResp;
import com.example.grapefield.chat.model.response.UserChatListEventResp;
import com.example.grapefield.chat.service.ChatParticipantService;
import com.example.grapefield.chat.service.UserChatListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatParticipantEventKafkaConsumer {

    private final ChatParticipantService participantService;
    private final UserChatListService userChatListService;

    // 참여자 수 변경 이벤트 처리
    @KafkaListener(
            topics = "chat-participant-events",
            groupId = "${spring.kafka.consumer.participant.group-id:chat-participant-group}",  // 환경변수 사용
            containerFactory = "participantEventKafkaListenerContainerFactory"
    )
    public void handleParticipantEvent(ChatParticipantEventResp event) {
        log.info("📨 참여자 이벤트 수신: {}", event);
        participantService.handleParticipantChange(event);
    }

    // 사용자 채팅방 리스트 이벤트 처리
    @KafkaListener(
            topics = "user-chatlist-events",
            groupId = "${spring.kafka.consumer.user-event.group-id:user-event-group}",  // 환경변수 사용
            containerFactory = "userEventKafkaListenerContainerFactory"
    )
    public void handleUserChatListEvent(UserChatListEventResp event) {
        log.info("📨 사용자 채팅방 리스트 이벤트 수신: {}", event);
        userChatListService.handleChatListUpdate(event);
    }
}