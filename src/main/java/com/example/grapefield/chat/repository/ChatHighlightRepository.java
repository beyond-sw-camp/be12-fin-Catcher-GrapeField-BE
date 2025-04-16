package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHighlightRepository extends JpaRepository<ChatHighlight, Long> {
    List<ChatHighlight> findAllByChatRoomIdxOrderByStartTimeAsc(Long roomIdx);
}
