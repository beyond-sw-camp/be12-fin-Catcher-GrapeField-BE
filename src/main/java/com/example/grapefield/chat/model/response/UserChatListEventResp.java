package com.example.grapefield.chat.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatListEventResp {
    private String type;
    private Long userIdx;
    private Long roomIdx;
    private String action;
    private Long timestamp;
}
