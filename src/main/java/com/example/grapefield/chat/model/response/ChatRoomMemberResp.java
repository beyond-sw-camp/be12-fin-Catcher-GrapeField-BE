package com.example.grapefield.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMemberResp {
    private Long userIdx;
    private String username;
    private LocalDateTime lastReadAt;
    private Boolean mute;
    private LocalDateTime lastActiveAt;
}
