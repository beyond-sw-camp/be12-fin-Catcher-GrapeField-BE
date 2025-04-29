package com.example.grapefield.notification.model.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    BEFORE_10MIN("시작 10분 전"),       // 시작 10분 전 알림
    BEFORE_1HOUR("1시간 전"),          // 시작 한 시간 전 알림
    DAY_9AM("당일 오전 9시"),           // 당일 오전 9시 알림
    CUSTOM_MESSAGE("기타 운영자 알림");   // 기타 운영자 알림

    private final String description;
    NotificationType(String description){
        this.description = description;
    }
}