package com.example.grapefield.chat.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "채팅 메시지 페이지 응답")
public class ChatMessagePageResp {
    private List<ChatMessageResp> content;
    private boolean hasNext;
}
