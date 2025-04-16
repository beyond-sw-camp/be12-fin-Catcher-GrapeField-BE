package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatHighlightResp {
    private Long idx;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private Long messageCnt;

    public static ChatHighlightResp fromEntity(ChatHighlight entity) {
        return ChatHighlightResp.builder()
                .idx(entity.getIdx())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .description(entity.getDescription())
                .messageCnt(entity.getMessageCnt())
                .build();
    }
}
