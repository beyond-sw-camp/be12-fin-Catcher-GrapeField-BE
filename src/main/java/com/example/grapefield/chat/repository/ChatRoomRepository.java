package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.events.model.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.events.category IN :categories")
    List<ChatRoom> findChatRoomsByCategoryIn(@Param("categories") List<EventCategory> categories);

}
