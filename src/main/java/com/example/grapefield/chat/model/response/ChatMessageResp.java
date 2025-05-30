package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "채팅 메시지 응답")
public class ChatMessageResp {
    @Schema(description = "메시지 고유 Idx", example = "101")
    private Long messageIdx;
    @Schema(description = "채팅방 Idx", example = "1")
    private Long roomIdx;
    @Schema(description = "보낸 사용자 Idx", example = "3")
    private Long userIdx; //    private Long sendUserIdx;
    @Schema(description = "보낸 사용자 이름", example = "포도햄")
    private String username;
    @Schema(description = "보낸 사용자 프로필 이미지", example = "https://cdn.example.com/user1.png")
    private String profileImageUrl;
    @Schema(description = "채팅 메시지 내용", example = "안녕하세요!")
    private String content;
    @Schema(description = "메시지 전송 시간", example = "2024-04-10T10:45:00")
    private LocalDateTime createdAt;
    @Schema(description = "하이라이브 메세지 여부", example = "false")
    private Boolean isHighlighted;

    public static ChatMessageResp from(ChatMessageCurrent msg) {
        ChatMessageResp resp = ChatMessageResp.builder()
                .messageIdx(msg.getMessageIdx())
                .roomIdx(msg.getChatRoom().getIdx())
                .userIdx(msg.getUser().getIdx())
                .username(msg.getUser().getUsername())
                .profileImageUrl(msg.getUser().getProfileImg())
                .content(msg.getContent())
                .createdAt(msg.getCreatedAt())
                .isHighlighted(msg.getIsHighlighted())
            .build();
        return resp;
    }

}
