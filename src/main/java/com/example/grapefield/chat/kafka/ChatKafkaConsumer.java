package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatKafkaConsumer {

    private final ChatMessageService chatMessageService;

    @KafkaListener(topicPattern = "chat-.*",
            groupId = "chat-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ChatMessageKafkaReq message) {
        log.info("✅ Kafka 메시지 수신: roomIdx={}, userIdx={}, content={}",
                message.getRoomIdx(), message.getSendUserIdx(), message.getContent());

        chatMessageService.saveMessage(message);
    }
}