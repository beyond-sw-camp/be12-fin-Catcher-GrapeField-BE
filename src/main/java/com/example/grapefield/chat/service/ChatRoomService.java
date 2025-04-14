package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AdminClient adminClient;

    public ChatRoom createRoom(Long roomIdx, String roomName) {
        // 1. DB에 채팅방 생성 (중복 방지)
        if (chatRoomRepository.existsById(roomIdx)) {
            log.info("채팅방 이미 존재: {}", roomIdx);
            return chatRoomRepository.findById(roomIdx).get();
        }

        ChatRoom room = ChatRoom.builder()
                .idx(roomIdx)
                .roomName(roomName)
                .createdAt(LocalDateTime.now())
                .heartCnt(0L)
                .build();
        chatRoomRepository.save(room);
        log.info("✅ 채팅방 DB 저장 완료");

        // 2. Kafka 토픽 생성
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

        return room;
    }
}

