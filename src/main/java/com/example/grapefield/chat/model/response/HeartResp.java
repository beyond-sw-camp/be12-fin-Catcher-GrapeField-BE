package com.example.grapefield.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeartResp {
    private Long roomIdx;
    private Long heartCount;
}
