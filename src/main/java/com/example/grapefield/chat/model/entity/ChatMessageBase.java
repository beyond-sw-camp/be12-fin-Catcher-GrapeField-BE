package com.example.grapefield.chat.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

//@Table(name="CHAT_MESSAGE_BASE")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessageBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageIdx;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "base", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private ChatMessageCurrent current;

    @OneToOne(mappedBy = "base", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private ChatMessageArchive archive;

    @OneToOne(mappedBy = "message")
    private ChatHighlight highlight;


}
