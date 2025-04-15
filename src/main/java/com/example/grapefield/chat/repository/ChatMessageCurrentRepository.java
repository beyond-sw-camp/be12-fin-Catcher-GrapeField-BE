package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ChatMessageCurrentRepository extends JpaRepository<ChatMessageCurrent, Long> {
    // 가장 최근 메시지 조회
    ChatMessageCurrent findTopByChatRoomOrderByCreatedAtDesc(ChatRoom room);

    // 유저가 참여한 모든 채팅방 입장 기록(ChatroomMember 엔티티) 조회
    int countByChatRoomAndCreatedAtAfter(ChatRoom room, LocalDateTime after);
}
