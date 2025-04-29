package com.example.grapefield.events.model.entity;

import lombok.Getter;

@Getter
public enum AgeLimit {
    ALL("전체 관람가"),
    TWELVE("12세 이상"),
    FIFTEEN("15세 이상"),
    NINETEEN("19세 이상");

    private final String description;

    AgeLimit(String description) {
        this.description = description;
    }
}
