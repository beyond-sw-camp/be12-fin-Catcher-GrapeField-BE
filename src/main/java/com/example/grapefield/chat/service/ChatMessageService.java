package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ProcessedMessage;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.repository.*;
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
    private final ProcessedMessageRepository processedMessageRepository;
    private final ChatMessageCurrentRepository chatMessageCurrentRepository;

    @Transactional
    public ChatMessageResp saveMessageIfNotProcessed(ChatMessageKafkaReq req) {
        String messageUuid = req.getMessageUuid();
        ChatMessageResp resp = new ChatMessageResp();
        if (processedMessageRepository.existsByMessageUuid(messageUuid)) {
            log.info("❌중복된 uuid 존재함.");
            ChatMessageCurrent newMessage = chatMessageCurrentRepository.findByMessageUuid(messageUuid);
            resp = ChatMessageResp.from(newMessage);
        } else {
            log.info("✅중복 uuid 없음: {}", messageUuid);
            resp = saveMessage(req, messageUuid);
            processedMessageRepository.save(new ProcessedMessage(messageUuid, LocalDateTime.now()));
        }
        return resp;
    }

    @Transactional
    public ChatMessageResp saveMessage(ChatMessageKafkaReq req, String messageUuid) {
        try {

            // 1. 채팅방 정보 가져오기
            ChatRoom room = chatRoomRepository.findById(req.getRoomIdx())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

            // 2. 사용자 정보 가져오기
            User user = userRepository.findById(req.getSendUserIdx())
                    .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            // role로 검증하는거 추가 검증이 되어야만 저장임


            // 3. 메시지 base 저장 (createdAt도 포함)
            ChatMessageBase base = ChatMessageBase.builder()
                    .createdAt(LocalDateTime.now())
                    .build();
            baseRepository.saveAndFlush(base); // flush로 영속성 컨텍스트에 반영

            log.info("✅ [1] base 메시지 저장됨 | baseId={}", base.getMessageIdx());

            // 4. 메시지 current 저장
            ChatMessageCurrent current = req.toEntity(base, room, user);
            currentRepository.save(current);

            log.info("✅ [2] current 메시지 저장됨 | baseId={}, user={}, room={}, content={}",
                    base.getMessageIdx(), user.getUsername(), room.getRoomName(), req.getContent());

            // 5. 저장된 메시지를 다시 한 번 직접 조회하여 검증
            boolean isSaved = currentRepository.findById(current.getBase().getMessageIdx()).isPresent();

            if (isSaved) {
                log.info("최종 저장 확인 완료 ✅ messageIdx={}", current.getBase().getMessageIdx());
            } else {
                log.warn("⚠️ current 메시지가 저장되지 않은 것 같습니다... messageIdx={}", current.getBase().getMessageIdx());
            }
            // 6. 새로운 메세지 송수신 양식 DTO로 바꿔서 반환
            ChatMessageResp resp = ChatMessageResp.builder()
                    .messageIdx(current.getMessageIdx())
                    .roomIdx(room.getIdx())
                    .userIdx(user.getIdx())
                    .username(user.getUsername())
                    .profileImageUrl(user.getProfileImg())
                    .content(current.getContent())
                    .createdAt(current.getCreatedAt())
                    .build();
            return resp;


        } catch (Exception e) {
            log.error("💥 메시지 저장 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }

    }
}