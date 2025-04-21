package com.example.grapefield.chat.model.request;


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
public class ChatHeartKafkaReq {
    @NotNull
    private Long roomIdx;
}
