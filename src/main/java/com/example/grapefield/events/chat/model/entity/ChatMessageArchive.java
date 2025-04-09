package com.example.grapefield.events.chat.model.entity;

import com.example.grapefield.user.model.entity.User;
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
public class ChatMessageArchive {
    @Id
    private Long messageIdx;

    @MapsId
    @OneToOne
    @JoinColumn(name = "message_idx")
    private ChatMessageBase base;

    @ManyToOne
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    private String content;
    private LocalDateTime createdAt;
    private Boolean isHighlighted;
    private String archiveDate;  // YYYYMM format
}
