package com.example.grapefield.user.model.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    START_REMINDER("오늘"),        // 시작 알림
    HOUR_REMINDER("1시간 전"),         // 시작 한 시간 전 알림
    CUSTOM_MESSAGE("기타");         // 기타 운영자 알림

    private final String description;
    NotificationType(String description){
        this.description = description;
    }
}