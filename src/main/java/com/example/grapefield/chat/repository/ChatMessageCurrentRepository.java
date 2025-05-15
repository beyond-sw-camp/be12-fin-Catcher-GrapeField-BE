package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageCurrentRepository extends JpaRepository<ChatMessageCurrent, Long> {
    // 가장 최근 메시지 순서로 역순 조회
    Page<ChatMessageCurrent> findByChatRoom_IdxOrderByCreatedAtDesc(Long roomIdx, Pageable pageable);

    // 유저가 참여한 모든 채팅방 입장 기록(ChatroomMember 엔티티) 조회
    int countByChatRoomAndCreatedAtAfter(ChatRoom room, LocalDateTime after);

    // ✅ 추가: 여러 채팅방의 최근 메시지 한 번에 조회
    @Query("""
        SELECT c FROM ChatMessageCurrent c
        WHERE c.messageIdx IN (
            SELECT MAX(c2.messageIdx) FROM ChatMessageCurrent c2
            WHERE c2.chatRoom IN :rooms
            GROUP BY c2.chatRoom
        )
        """)
    List<ChatMessageCurrent> findLatestMessagesByRooms(@Param("rooms") List<ChatRoom> rooms);

    ChatMessageCurrent findByMessageUuid(String messageUuid);
}
