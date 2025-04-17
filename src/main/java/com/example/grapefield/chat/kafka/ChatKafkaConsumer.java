package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatKafkaConsumer {

    @KafkaListener(topicPattern = "chat-.*",
            groupId = "chat-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ChatMessageResp message) {
        log.info("✅ Kafka 메시지 수신: roomIdx={}, userIdx={}, content={}",
                message.getRoomIdx(), message.getUserIdx(), message.getContent());
    }
}

