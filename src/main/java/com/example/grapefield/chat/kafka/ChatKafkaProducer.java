package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatRoomService chatRoomService;
    // private final KafkaTopicService kafkaTopicService;

    public void sendMessage(ChatMessageKafkaReq chatMessageKafkaReq) {
        // kafkaTopicService.createKafkaTopicIfNotExists(chatMessageKafkaReq.getRoomIdx());
        String topic = "chat-" + chatMessageKafkaReq.getRoomIdx();
        log.info("✅ KafkaProducer 발행: send message'{}' to topic'{}'", topic, chatMessageKafkaReq);
        kafkaTemplate.send(topic, chatMessageKafkaReq);
    }

    public void likeRoom(ChatHeartKafkaReq chatHeartKafkaReq) {
        // kafkaTopicService.createHeartKafkaTopicIfNotExist(chatHeartKafkaReq);
        String topic = "chat-like-" + chatHeartKafkaReq.getRoomIdx();
        log.info("✅ KafkaProducer 발행: send heart ♥️ message'{}' to topic'{}'", chatHeartKafkaReq, topic);
        kafkaTemplate.send(topic, chatHeartKafkaReq);
    }
}
