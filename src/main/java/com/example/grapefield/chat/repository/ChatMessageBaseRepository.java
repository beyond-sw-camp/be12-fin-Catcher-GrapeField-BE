package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageBase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageBaseRepository extends JpaRepository<ChatMessageBase, Long> {
}
