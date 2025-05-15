package com.example.grapefield.chat.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@Schema(description = "채팅 메시지 전송 요청")
@ToString
public class ChatMessageReq {

    @Schema(description = "채팅방 ID", example = "1", required = true)
    @NotNull
    private Long roomIdx;

    @Schema(description = "채팅 메시지 내용", example = "안녕하세요!", required = true)
    @NotBlank
    private String content;
}
