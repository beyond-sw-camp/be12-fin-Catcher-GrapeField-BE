package com.example.demo.base.model.response;

import com.example.demo.base.model.Base;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseRespDto {
    private String content;
    public static BaseRespDto from(Base base) {
        return BaseRespDto.builder()
                .content(base.getContent())
                .build();
    }
}
