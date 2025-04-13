package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageCurrentRepository extends JpaRepository<ChatMessageCurrent, Long> {
}
