package com.example.grapefield.chat.model.entity;

import com.example.grapefield.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

//@Table(name="CHAT_MESSAGE_CURRENT")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "chat_message_current", uniqueConstraints = @UniqueConstraint(columnNames = "message_uuid"))
public class ChatMessageCurrent {
    @Id
    private Long messageIdx;

    @Column(name = "message_uuid", nullable = false, unique = true)
    private String messageUuid; // JPA 유니크 제약 설정

    @MapsId
    @OneToOne
    @JoinColumn(name = "message_idx")
    private ChatMessageBase base;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isHighlighted = false;
}
