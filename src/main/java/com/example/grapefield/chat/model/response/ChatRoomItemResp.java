package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.user.model.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatRoomItemResp {
    private Long roomIdx;
    private String roomName;
    private String title;
    private String imageUrl;

    private List<ChatRoomMessageResp> messages;
    private List<ChatHighlightResp> highlights;

    public static ChatRoomItemResp fromEntity(ChatRoom room, List<ChatRoomMessageResp> messages, List<ChatHighlightResp> highlights) {
        return ChatRoomItemResp.builder()
                .roomIdx(room.getIdx())
                .roomName(room.getRoomName())
                .title(room.getRoomName())
                .imageUrl(room.getEvents().getPosterImgUrl())
                .messages(messages)
                .highlights(highlights)
                .build();
    }
}
