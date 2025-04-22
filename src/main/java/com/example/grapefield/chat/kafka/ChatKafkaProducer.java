package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(ChatMessageKafkaReq chatMessageKafkaReq) {
        String topic = "chat-" + chatMessageKafkaReq.getRoomIdx();
        log.info("✅ KafkaProducer 발행: send message'{}' to topic'{}'", topic, chatMessageKafkaReq);
        kafkaTemplate.send(topic, chatMessageKafkaReq);
    }
}
