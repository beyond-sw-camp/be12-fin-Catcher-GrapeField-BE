package com.example.grapefield.chat.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

@Getter
@Schema(description = "채팅 메시지 전송 요청")
public class ChatMessageReq {

    @Schema(description = "채팅방 ID", example = "1", required = true)
    @NotNull
    private Long roomIdx;

    /*
    @JsonIgnore // 혹은 @Schema(hidden = ture) 혹은 아예 필드 지우기
    @Schema(description = "보내는 사용자 ID", example = "3", required = true)
    @NotNull
    private Long sendUserIdx;

     */

    @Schema(description = "채팅 메시지 내용", example = "안녕하세요!", required = true)
    @NotBlank
    private String content;
}
