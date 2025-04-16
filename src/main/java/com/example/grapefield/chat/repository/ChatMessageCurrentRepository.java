package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChatMessageCurrentRepository extends JpaRepository<ChatMessageCurrent, Long> {
    Collection<Object> findByChatRoom(ChatRoom chatRoom);
    List<ChatMessageCurrent> findAllByChatRoomIdxOrderByChatRoomIdxAsc(Long roomIdx);
}
