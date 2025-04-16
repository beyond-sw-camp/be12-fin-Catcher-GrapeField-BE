package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMessageResp {
    private Long messageIdx;
    private Long userIdx;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isHighlighted;

    public static ChatRoomMessageResp fromEntity(ChatMessageCurrent entity) {
        return ChatRoomMessageResp.builder()
                .messageIdx(entity.getMessageIdx())
                .userIdx(entity.getUser().getIdx())
                .username(entity.getUser().getUsername())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .isHighlighted(entity.getIsHighlighted())
                .build();
    }
}
