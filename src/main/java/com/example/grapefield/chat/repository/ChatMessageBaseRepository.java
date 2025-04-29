package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageBaseRepository extends JpaRepository<ChatMessageBase, Long> {
    /**
     * 채팅방 별 최신 ChatMessageBase 를 조회
     * JOIN을 사용해 ChatMessageCurrent와 매핑된 Base만 가져옵니다.
     */
    @Query(value = "SELECT b.* FROM chat_message_base b " +
            "JOIN chat_message_current c ON c.message_idx = b.message_idx " +
            "WHERE c.room_idx = :roomIdx " +
            "ORDER BY b.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<ChatMessageBase> findTopByRoomIdx(@Param("roomIdx") Long roomIdx);
}
