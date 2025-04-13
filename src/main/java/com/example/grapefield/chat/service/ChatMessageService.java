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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageBaseRepository baseRepository;
    private final ChatMessageCurrentRepository currentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void saveMessage(ChatMessageKafkaReq req) {
        // 1. 채팅방 정보 가져오기
        ChatRoom room = chatRoomRepository.findById(req.getRoomIdx())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 2. 사용자 정보 가져오기
        User user = userRepository.findById(req.getSendUserIdx())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 3. 메시지 base 저장 (자동 생성된 IDX 사용)
        ChatMessageBase base = baseRepository.save(ChatMessageBase.builder().build());

        // 4. 메시지 current 저장 (base 포함)
        ChatMessageCurrent current = ChatMessageCurrent.builder()
                .base(base)
                .chatRoom(room)
                .user(user)
                .content(req.getContent())
                .createdAt(base.getCreatedAt())
                .isHighlighted(false)
                .build();

        currentRepository.save(current);

        // 로그 확인
        log.info("✅ 채팅 메시지 저장 완료 | baseId={}, user={}, room={}, content={}",
                base.getMessageIdx(), user.getUsername(), room.getRoomName(), req.getContent());
    }
}
