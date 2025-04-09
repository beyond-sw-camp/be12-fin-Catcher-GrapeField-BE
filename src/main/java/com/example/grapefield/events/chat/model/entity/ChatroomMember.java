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
public class ChatroomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "chatroom_idx")
    private ChatRoom chatRoom;
    private LocalDateTime lastReadAt;
    private Boolean mute;
    private LocalDateTime lastActiveAt;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

}
