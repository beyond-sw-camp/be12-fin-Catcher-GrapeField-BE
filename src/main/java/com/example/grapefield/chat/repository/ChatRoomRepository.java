package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
