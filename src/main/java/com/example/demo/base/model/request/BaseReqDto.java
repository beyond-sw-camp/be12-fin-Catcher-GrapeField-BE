package com.example.demo.base.model.request;

import com.example.demo.base.model.Base;
import lombok.Getter;

@Getter
public class BaseReqDto {
    private Long idx;
    private String content;

    public Base toEntity(){
        return Base.builder()
                .idx(idx)
                .content(content)
                .build();
    }
}
