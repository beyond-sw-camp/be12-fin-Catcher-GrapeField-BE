package com.example.grapefield.events.model.entity;

import lombok.Getter;

@Getter
public enum EventCategory {
    ALL("ALL"),
    MUSICAL("MUSICAL"),
    PLAY("PLAY"),
    CONCERT("CONCERT"),
    EXHIBITION("EXHIBITION"),
    CLASSIC("CLASSIC");

    private final String description;

    EventCategory(String description) {
        this.description = description;
    }
}
