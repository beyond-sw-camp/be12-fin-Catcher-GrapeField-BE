package com.example.grapefield.chat.model.entity;

import jakarta.persistence.*;
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
public class ChatHighlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private Long messageCnt;

    @OneToOne
    @JoinColumn(name = "message_idx", unique = true)
    private ChatMessageBase message;
}
