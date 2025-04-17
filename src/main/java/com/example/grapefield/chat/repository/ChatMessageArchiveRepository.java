package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatMessageArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageArchiveRepository extends JpaRepository<ChatMessageArchive, Long> {
}
