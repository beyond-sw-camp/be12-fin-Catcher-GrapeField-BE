package com.example.grapefield.events.participant.model.entity;

import lombok.Getter;

@Getter
public enum AssociationType {
    HOST("주최"),
    ORGANIZER("주관"),
    SPONSOR("후원"),
    PARTICIPANT("참가");

    private final String description;

    AssociationType(String description) {
        this.description = description;
    }
}
