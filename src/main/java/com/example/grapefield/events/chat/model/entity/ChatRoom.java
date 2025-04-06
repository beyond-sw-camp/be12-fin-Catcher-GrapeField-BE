package com.example.grapefield.events.chat.model.entity;

import com.example.grapefield.events.model.entity.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatRoom {
    @Id
    private Long idx;

    @MapsId
    @OneToOne
    @JoinColumn(name = "events_idx")
    private Events events;

    private String roomName;
    private LocalDateTime createdAt;
    private Long heartCnt;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatroomMember> memberList;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessageCurrent> currentMessageList;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatHighlight> highlightList;
}
