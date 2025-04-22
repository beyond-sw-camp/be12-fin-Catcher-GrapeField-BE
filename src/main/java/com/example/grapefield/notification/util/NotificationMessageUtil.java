package com.example.grapefield.notification.util;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.*;

/**
 * 알림 메시지 생성을 위한 유틸리티 클래스
 */
public class NotificationMessageUtil {

    /**
     * 알림 객체로부터 메시지 생성
     */
    public static String buildMessage(ScheduleNotification notification) {
        if (notification == null) {
            return "알림이 도착했습니다.";
        }

        if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST &&
                notification.getEventsInterest() != null) {

            EventsInterest interest = notification.getEventsInterest();
            Events event = interest.getEvents();
            if (event == null) return "알림이 도착했습니다.";

            return buildEventMessage(event.getTitle(), notification.getNotificationType());
        } else if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE &&
                notification.getPersonalSchedule() != null) {

            PersonalSchedule schedule = notification.getPersonalSchedule();
            if (schedule == null) return "알림이 도착했습니다.";

            return buildScheduleMessage(schedule.getTitle(), notification.getNotificationType());
        }

        return "알림이 도착했습니다.";
    }

    /**
     * 이벤트 알림 메시지 생성
     */
    public static String buildEventMessage(String eventTitle, NotificationType type) {
        if (eventTitle == null) {
            eventTitle = "이벤트";
        }

        switch (type) {
            case BEFORE_10MIN:
                return String.format("'%s' 공연/전시가 10분 후에 시작합니다.", eventTitle);
            case BEFORE_1HOUR:
                return String.format("'%s' 공연/전시가 1시간 후에 시작합니다.", eventTitle);
            case DAY_9AM:
                return String.format("오늘은 '%s' 공연/전시 시작일입니다.", eventTitle);
            case CUSTOM_MESSAGE:
                return String.format("'%s' 관련 소식이 있습니다.", eventTitle);
            default:
                return "알림이 도착했습니다.";
        }
    }

    /**
     * 개인 일정 알림 메시지 생성
     */
    public static String buildScheduleMessage(String scheduleTitle, NotificationType type) {
        if (scheduleTitle == null) {
            scheduleTitle = "일정";
        }

        switch (type) {
            case BEFORE_10MIN:
                return String.format("'%s' 일정이 10분 후에 시작합니다.", scheduleTitle);
            case BEFORE_1HOUR:
                return String.format("'%s' 일정이 1시간 후에 시작합니다.", scheduleTitle);
            case DAY_9AM:
                return String.format("오늘은 '%s' 일정이 있는 날입니다.", scheduleTitle);
            case CUSTOM_MESSAGE:
                return String.format("'%s' 일정 관련 소식이 있습니다.", scheduleTitle);
            default:
                return String.format("'%s' 일정 알림이 도착했습니다.", scheduleTitle);
        }
    }
}