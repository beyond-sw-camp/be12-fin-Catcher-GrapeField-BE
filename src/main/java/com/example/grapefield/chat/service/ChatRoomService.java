package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.events.model.entity.Events;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final AdminClient adminClient;
    private final ChatRoomMemberRepository memberRepository;
    private final ChatMessageCurrentRepository currentRepository;

    /*
    // 초기 개발 테스트용
    // 채팅방(ChatRoom)이 DB에 없으면 새로 만들고, Kafka 토픽도 같이 보장
    public ChatRoom ensureRoomExists(Long roomIdx, String roomName) {
        return chatRoomRepository.findById(roomIdx).orElseGet(() -> {
            ChatRoom room = ChatRoom.builder()
                    .idx(roomIdx)
                    .roomName(roomName)
                    .createdAt(LocalDateTime.now())
                    .heartCnt(0L)
                    .build();

            chatRoomRepository.save(room);
            log.info("✅ 채팅방 DB 저장 완료: {}", roomIdx);

            // Kafka 토픽도 보장
            createKafkaTopicIfNotExists(roomIdx);

            return room;
        });
    }

    // Kafka에 해당 채팅방 토픽이 없으면 새로 생성
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

    */

    public ChatRoom findByIdx(Long roomIdx) {
        return chatRoomRepository.findById(roomIdx)
                .orElseThrow(()->
                        new NoSuchElementException("해당 채팅방이 존재하지 않습니다. roomIdx: " + roomIdx));
    }


    @Transactional
    public void increaseHeartCount(Long roomIdx) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        chatRoom.increaseHeart(); // heartCnt += 1
    }

}
