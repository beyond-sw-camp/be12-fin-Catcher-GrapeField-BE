package com.example.grapefield.chat.kafka;

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

    public void sendMessage(ChatMessageResp chatMessageResp) {
        String topic = "chat-" + chatMessageResp.getRoomIdx();
        log.info("Kafka 발행: send message'{}' to topic'{}'", topic, chatMessageResp);
        kafkaTemplate.send(topic, chatMessageResp);
    }
}
