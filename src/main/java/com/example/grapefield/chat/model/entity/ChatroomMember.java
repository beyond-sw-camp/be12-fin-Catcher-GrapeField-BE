package com.example.grapefield.chat.model.entity;

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

    public void updateLastActiveAt(LocalDateTime newTime) {
        this.lastActiveAt = newTime;
    }

    // (추후 필요하면 읽음 시간 갱신도 추가 가능)
    public void updateLastReadAt(LocalDateTime time) {
        this.lastReadAt = time;
    }

}
