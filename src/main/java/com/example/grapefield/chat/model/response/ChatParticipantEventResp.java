package com.example.grapefield.chat.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatParticipantEventResp {
    private String type;
    private Long roomIdx;
    private String action;
    private Long timestamp;
}
