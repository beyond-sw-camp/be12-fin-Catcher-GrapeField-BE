package com.example.grapefield.chat.model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Valid
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카프카 하트 전송용")
public class ChatHeartKafkaReq {
    @NotNull
    @Schema(description = "채팅방 ID", example = "1", required = true)
    private Long roomIdx;
}
