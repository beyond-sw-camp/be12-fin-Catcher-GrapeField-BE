package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHighlightRepository extends JpaRepository<ChatHighlight, Long> {
}
