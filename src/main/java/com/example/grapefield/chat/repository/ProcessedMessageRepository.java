package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
    Boolean existsByMessageUuid(String messageId);
}
