package com.example.demo.events.model.entity;

import lombok.Getter;

@Getter
public enum EventCategory {
    MUSICAL("뮤지컬"),
    PLAY("연극"),
    CONCERT("콘서트"),
    EXHIBITION("전시회"),
    FAIR("박람회");

    private final String description;

    EventCategory(String description) {
        this.description = description;
    }
}
