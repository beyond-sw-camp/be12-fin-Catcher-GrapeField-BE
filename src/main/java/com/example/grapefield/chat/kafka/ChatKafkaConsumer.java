package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatKafkaConsumer {

    private final ChatMessageService chatMessageService; //추가
    private final SimpMessagingTemplate simpMessagingTemplate; //추가


    @KafkaListener(topicPattern = "^chat-\\d+$",
            groupId = "${spring.kafka.consumer.chat.group-id}",
            containerFactory = "chatKafkaListenerContainerFactory")
    public void consume(ChatMessageKafkaReq chatMessageKafkaReq) { //매개변수 리팩터링
        log.info("Kafka 메시지 수신: roomIdx={}, userIdx={}, content={}",
                chatMessageKafkaReq.getRoomIdx(), chatMessageKafkaReq.getSendUserIdx(), chatMessageKafkaReq.getContent());


        ChatMessageResp resp = chatMessageService.saveMessage(chatMessageKafkaReq); //DB 저장 로직 추가

        simpMessagingTemplate.convertAndSend("/topic/chat.room." + resp.getRoomIdx(), resp); // WebSocket broadcast 로직 추가

        log.info("WebSocket Broadcast -> roomIdx: {}, sendUserIdx: {}, content: {} ", resp.getRoomIdx(), resp.getUserIdx(), resp.getContent()); //로그 추가

    }
}