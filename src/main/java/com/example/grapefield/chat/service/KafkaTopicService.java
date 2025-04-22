package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaTopicService {
    private final AdminClient adminClient;

    public void createHeartKafkaTopicIfNotExist(ChatHeartKafkaReq chatHeartKafkaReq) {
        String topicName = "chat-like-" + chatHeartKafkaReq.getRoomIdx();
        try {
            var existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(topicName)) {
                adminClient.createTopics(List.of(new NewTopic(topicName, 1, (short) 1)));
                log.info("✅ Kafka 토픽 생성 완료: {}", topicName);
            } else {
                log.info("ℹ️ Kafka 토픽 이미 존재: {}", topicName);
            }
        } catch (Exception e) {
            log.warn("⚠️ Kafka 토픽 생성 중 에러: {}", e.getMessage());
        }
    }

    public void createKafkaTopicIfNotExists(Long roomIdx) {
        String topicName = "chat-" + roomIdx;
        try {
            var existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(topicName)) {
                adminClient.createTopics(List.of(new NewTopic(topicName, 1, (short) 1)));
                log.info("✅ Kafka 토픽 생성 완료: {}", topicName);
            } else {
                log.info("ℹ️ Kafka 토픽 이미 존재: {}", topicName);
            }
        } catch (Exception e) {
            log.warn("⚠️ Kafka 토픽 생성 중 에러: {}", e.getMessage());
        }
    }

}
