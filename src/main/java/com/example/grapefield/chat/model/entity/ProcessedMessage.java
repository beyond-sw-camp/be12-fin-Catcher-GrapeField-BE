package com.example.grapefield.chat.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "processed_message", uniqueConstraints = @UniqueConstraint(columnNames = "message_uuid"))
public class ProcessedMessage {
    @Id
    private String messageUuid;
    private LocalDateTime processedAt;
}
