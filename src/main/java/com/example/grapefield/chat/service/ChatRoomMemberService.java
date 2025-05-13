package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.model.response.ChatParticipantEventResp;
import com.example.grapefield.chat.model.response.UserChatListEventResp;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomMemberService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomMemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private String getRedisKey(Long roomIdx) {
        return "chat:room:" + roomIdx + ":participants";
    }

    @Transactional
    public void joinRoom(Long userIdx, Long roomIdx) {
        try {
            ChatRoom room = chatRoomRepository.findById(roomIdx)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
            User user = userRepository.findById(userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            boolean exists = memberRepository.existsByChatRoomAndUser(room, user);

            if (!exists) {
                ChatroomMember newMember = ChatroomMember.builder()
                        .chatRoom(room)
                        .user(user)
                        .lastActiveAt(LocalDateTime.now())
                        .lastReadAt(null)
                        .mute(false)
                        .build();
                memberRepository.save(newMember);
                log.info("🆕 최초입장 → ChatroomMember 저장됨");

                // 트랜잭션 커밋 후 Redis + 카프카 처리
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                // 1. Redis 카운트 증가
                                String key = getRedisKey(roomIdx);
                                redisTemplate.opsForValue().increment(key);

                                // 2. 참여자 수 카프카 이벤트 발행
                                sendParticipantChangeEvent(roomIdx, "JOIN");

                                // 3. 사용자 채팅방 리스트 카프카 이벤트 발행
                                sendUserChatListEvent(userIdx, roomIdx, "JOIN");
                            }
                        }
                );
            } else {
                log.info("✅ 이미 참여 중 → 아무 처리 안함");
            }
        } catch (Exception e) {
            log.error("❌ joinRoom 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 채팅방 퇴장
    @Transactional
    public void leaveRoom(Long userIdx, Long roomIdx) {
        ChatRoom room = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        ChatroomMember member = memberRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalStateException("이미 퇴장한 채팅방입니다."));

        memberRepository.delete(member);
        log.info("👋 퇴장 완료 - userIdx: {}, roomIdx: {}", userIdx, roomIdx);

        // 트랜잭션 커밋 후 Redis + 카프카 처리
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // 1. Redis 카운트 감소
                        String key = getRedisKey(roomIdx);
                        redisTemplate.opsForValue().decrement(key);

                        // 2. 참여자 수 카프카 이벤트 발행
                        sendParticipantChangeEvent(roomIdx, "LEAVE");

                        // 3. 사용자 채팅방 리스트 카프카 이벤트 발행
                        sendUserChatListEvent(userIdx, roomIdx, "LEAVE");
                    }
                }
        );
    }

    // 참여자 수 변경 이벤트 발행
    private void sendParticipantChangeEvent(Long roomIdx, String action) {
        try {
            ChatParticipantEventResp event = new ChatParticipantEventResp();
            event.setType("PARTICIPANT_CHANGE");
            event.setRoomIdx(roomIdx);
            event.setAction(action);
            event.setTimestamp(System.currentTimeMillis());

            kafkaTemplate.send("chat-participant-events", event);
            log.info("📤 참여자 수 카프카 이벤트 전송: {}", event);
        } catch (Exception e) {
            log.error("❌ 참여자 수 카프카 전송 실패: {}", e.getMessage(), e);
        }
    }


    // 사용자 채팅방 리스트 이벤트 발행
    private void sendUserChatListEvent(Long userIdx, Long roomIdx, String action) {
        try {
            UserChatListEventResp event = new UserChatListEventResp();
            event.setType("USER_CHATLIST_UPDATE");
            event.setUserIdx(userIdx);
            event.setRoomIdx(roomIdx);
            event.setAction(action);
            event.setTimestamp(System.currentTimeMillis());

            kafkaTemplate.send("user-chatlist-events", event);
            log.info("📤 사용자 채팅방 리스트 카프카 이벤트 전송: {}", event);
        } catch (Exception e) {
            log.error("❌ 사용자 채팅방 리스트 카프카 전송 실패: {}", e.getMessage(), e);
        }
    }


    public int getParticipantCount(Long roomIdx) {
        String key = getRedisKey(roomIdx);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Integer.parseInt(cached);
        }

        // 캐시에 없으면 DB에서 조회하고 Redis에 저장
        int count = memberRepository.countByChatRoom_Idx(roomIdx);
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        redisTemplate.expire(key, Duration.ofHours(24));
        return count;
    }

    // getParticipantCountMap() 메서드는 deprecated로 표시
    @Deprecated
    public Map<Long, Integer> getParticipantCountMap() {
        log.warn("⚠️ getParticipantCountMap() 사용됨 - Redis 기반으로 변경 권장");
        return memberRepository.countParticipantsGroupedByRoom().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

}
