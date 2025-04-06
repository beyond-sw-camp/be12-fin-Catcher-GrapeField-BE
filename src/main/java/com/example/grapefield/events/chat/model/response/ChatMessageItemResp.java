package com.example.grapefield.events.chat.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "채팅 메시지 응답 항목 DTO")
public class ChatMessageItemResp {
    @Schema(description = "보낸 사용자 ID", example = "3")
    private Long userIdx;
    @Schema(description = "닉네임", example = "햄이")
    private String nickname;
    @Schema(description = "메시지 내용", example = "식아 잘 자~")
    private String content;
    @Schema(description = "전송 시간", example = "2025-04-10T00:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "강조 여부", example = "false")
    private Boolean isHighlighted;
}
