package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.repository.ChatMessageBaseRepository;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageBaseRepository baseRepository;
    private final ChatMessageCurrentRepository currentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveMessage(ChatMessageKafkaReq req) {
        try {
        // 1. 채팅방 정보 가져오기
        ChatRoom room = chatRoomRepository.findById(req.getRoomIdx())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 2. 사용자 정보 가져오기
        User user = userRepository.findById(req.getSendUserIdx())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 3. 메시지 base 저장 (createdAt도 포함)
        ChatMessageBase base = ChatMessageBase.builder()
                .createdAt(LocalDateTime.now())
                .build();
        baseRepository.saveAndFlush(base); // flush로 영속성 컨텍스트에 반영

        log.info("✅ [1] base 메시지 저장됨 | baseId={}", base.getMessageIdx());

        // 4. 메시지 current 저장
        ChatMessageCurrent current = ChatMessageCurrent.builder()
                .base(base) // 반드시 방금 저장한 base 사용
                .chatRoom(room)
                .user(user)
                .content(req.getContent())
                .createdAt(base.getCreatedAt()) // 정렬을 위해 동일 시간 사용
                .isHighlighted(false)
                .build();

        currentRepository.save(current);

        log.info("✅ [2] current 메시지 저장됨 | baseId={}, user={}, room={}, content={}",
                base.getMessageIdx(), user.getUsername(), room.getRoomName(), req.getContent());

            // 5. 저장된 메시지를 다시 한 번 직접 조회하여 검증
            boolean isSaved = currentRepository.findById(current.getBase().getMessageIdx()).isPresent();

            if (isSaved) {
                log.info("🎉 최종 저장 확인 완료! ✅ messageIdx={}", current.getBase().getMessageIdx());
            } else {
                log.warn("⚠️ current 메시지가 저장되지 않은 것 같습니다... messageIdx={}", current.getBase().getMessageIdx());
            }

        } catch (Exception e) {
            log.error("💥 메시지 저장 중 예외 발생: {}", e.getMessage(), e);
            throw e; // rollback 유도
        }

    }
}