package com.example.demo.events.post.model.entity;

import lombok.Getter;

@Getter
public enum PostType {
    NOTICE("공지"),
    CHAT("잡담"),
    INFO("정보"),
    REVIEW("후기"),
    QUESTION("질문");

    private final String description;

    PostType(String description) {
        this.description = description;
    }
}
