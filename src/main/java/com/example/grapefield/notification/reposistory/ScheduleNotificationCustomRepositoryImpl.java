package com.example.grapefield.notification.reposistory;

import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.notification.model.entity.QEventsInterest;
import com.example.grapefield.notification.model.entity.QPersonalSchedule;
import com.example.grapefield.notification.model.entity.QScheduleNotification;
import com.example.grapefield.notification.model.entity.ScheduleType;
import com.example.grapefield.notification.model.response.NotificationResp;
import com.example.grapefield.user.model.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ScheduleNotificationCustomRepositoryImpl implements ScheduleNotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    // 날짜 포맷팅 유틸리티 메소드
    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(dateTime, now);

            if (duration.isNegative()) {
                // 미래 시간인 경우
                duration = duration.negated();
                if (duration.toMinutes() < 60) {
                    return duration.toMinutes() + "분 후";
                } else if (duration.toHours() < 24) {
                    return duration.toHours() + "시간 후";
                } else {
                    return duration.toDays() + "일 후";
                }
            } else {
                // 과거 시간인 경우
                if (duration.toMinutes() < 1) {
                    return "방금 전";
                } else if (duration.toHours() < 1) {
                    return duration.toMinutes() + "분 전";
                } else if (duration.toDays() < 1) {
                    return duration.toHours() + "시간 전";
                } else if (duration.toDays() < 7) {
                    return duration.toDays() + "일 전";
                } else {
                    return DateTimeFormatter.ofPattern("yyyy.MM.dd").format(dateTime);
                }
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public List<NotificationResp> findNotificationsByUserIdx(Long userIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QUser user = QUser.user;
        QEventsInterest eventsInterest = QEventsInterest.eventsInterest;
        QEvents events = QEvents.events;
        QPersonalSchedule personalSchedule = QPersonalSchedule.personalSchedule;

        return queryFactory
                .select(
                        Projections.constructor(
                                NotificationResp.class,
                                notification.idx,
                                notification.user.idx,
                                // 알림 유형에 따라 제목 생성
                                new CaseBuilder()
                                        .when(notification.scheduleType.eq(ScheduleType.EVENTS_INTEREST))
                                        .then("공연/전시 알림")
                                        .when(notification.scheduleType.eq(ScheduleType.PERSONAL_SCHEDULE))
                                        .then("개인 일정 알림")
                                        .otherwise("알림"),
                                // 메시지 필드는 애플리케이션 레벨에서 처리(복잡한 로직)
                                Expressions.asString("알림이 도착했습니다."), // 기본값, 후처리에서 수정
                                notification.notificationTime,
                                notification.isRead,
                                notification.notificationType.stringValue(),
                                notification.scheduleType.stringValue(),
                                // formattedTime 필드는 후처리에서 계산
                                Expressions.asString("")
                        )
                )
                .from(notification)
                .leftJoin(notification.user, user)
                .leftJoin(notification.eventsInterest, eventsInterest)
                .leftJoin(eventsInterest.events, events)
                .leftJoin(notification.personalSchedule, personalSchedule)
                .where(notification.user.idx.eq(userIdx).and(notification.isVisible.eq(true)))
                .orderBy(notification.notificationTime.desc())
                .fetch()
                .stream()
                .map(this::enrichNotification) // 후처리로 메시지와 formattedTime 설정
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResp> findUnreadNotificationsByUserIdx(Long userIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QUser user = QUser.user;
        QEventsInterest eventsInterest = QEventsInterest.eventsInterest;
        QEvents events = QEvents.events;
        QPersonalSchedule personalSchedule = QPersonalSchedule.personalSchedule;

        return queryFactory
                .select(
                        Projections.constructor(
                                NotificationResp.class,
                                notification.idx,
                                notification.user.idx,
                                // 알림 유형에 따라 제목 생성
                                new CaseBuilder()
                                        .when(notification.scheduleType.eq(ScheduleType.EVENTS_INTEREST))
                                        .then("공연/전시 알림")
                                        .when(notification.scheduleType.eq(ScheduleType.PERSONAL_SCHEDULE))
                                        .then("개인 일정 알림")
                                        .otherwise("알림"),
                                // 메시지 필드는 애플리케이션 레벨에서 처리(복잡한 로직)
                                Expressions.asString("알림이 도착했습니다."), // 기본값, 후처리에서 수정
                                notification.notificationTime,
                                notification.isRead,
                                notification.notificationType.stringValue(),
                                notification.scheduleType.stringValue(),
                                // formattedTime 필드는 후처리에서 계산
                                Expressions.asString("")
                        )
                )
                .from(notification)
                .leftJoin(notification.user, user)
                .leftJoin(notification.eventsInterest, eventsInterest)
                .leftJoin(eventsInterest.events, events)
                .leftJoin(notification.personalSchedule, personalSchedule)
                .where(notification.user.idx.eq(userIdx)
                        .and(notification.isRead.eq(false))
                        .and(notification.isVisible.eq(true)))
                .orderBy(notification.notificationTime.desc())
                .fetch()
                .stream()
                .map(this::enrichNotification) // 후처리로 메시지와 formattedTime 설정
                .collect(Collectors.toList());
    }

    // 추가 정보 설정 (메시지 및 formattedTime)
    private NotificationResp enrichNotification(NotificationResp resp) {
        // 적절한 메시지 생성
        resp.setFormattedTime(formatTimeAgo(resp.getNotificationTime()));

        // scheduleType과 notificationType에 따른 메시지 생성
        // 필요한 경우 추가 쿼리로 이벤트/일정 제목을 가져와 메시지 완성
        if ("EVENTS_INTEREST".equals(resp.getScheduleType())) {
            String eventTitle = fetchEventTitle(resp.getIdx());
            resp.setMessage(buildEventMessage(eventTitle, resp.getNotificationType()));

            // 이벤트 최소 정보 설정
            NotificationResp.EventMinimalInfo eventInfo = fetchEventInfo(resp.getIdx());
            resp.setEventInfo(eventInfo);
        } else if ("PERSONAL_SCHEDULE".equals(resp.getScheduleType())) {
            String scheduleTitle = fetchScheduleTitle(resp.getIdx());
            resp.setMessage(buildScheduleMessage(scheduleTitle, resp.getNotificationType()));

            // 일정 최소 정보 설정
            NotificationResp.ScheduleMinimalInfo scheduleInfo = fetchScheduleInfo(resp.getIdx());
            resp.setScheduleInfo(scheduleInfo);
        }

        return resp;
    }

    // 이벤트 정보 조회 (별도 쿼리)
    private String fetchEventTitle(Long notificationIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QEventsInterest eventsInterest = QEventsInterest.eventsInterest;
        QEvents events = QEvents.events;

        return queryFactory
                .select(events.title)
                .from(notification)
                .join(notification.eventsInterest, eventsInterest)
                .join(eventsInterest.events, events)
                .where(notification.idx.eq(notificationIdx))
                .fetchOne();
    }

    // 일정 정보 조회 (별도 쿼리)
    private String fetchScheduleTitle(Long notificationIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QPersonalSchedule personalSchedule = QPersonalSchedule.personalSchedule;

        return queryFactory
                .select(personalSchedule.title)
                .from(notification)
                .join(notification.personalSchedule, personalSchedule)
                .where(notification.idx.eq(notificationIdx))
                .fetchOne();
    }

    // 이벤트 최소 정보 조회
    private NotificationResp.EventMinimalInfo fetchEventInfo(Long notificationIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QEventsInterest eventsInterest = QEventsInterest.eventsInterest;
        QEvents events = QEvents.events;

        Tuple result = queryFactory
                .select(
                        events.idx,
                        events.title,
                        events.venue
                )
                .from(notification)
                .join(notification.eventsInterest, eventsInterest)
                .join(eventsInterest.events, events)
                .where(notification.idx.eq(notificationIdx))
                .fetchOne();

        if (result != null) {
            return NotificationResp.EventMinimalInfo.builder()
                    .idx(result.get(events.idx))
                    .title(result.get(events.title))
                    .venue(result.get(events.venue))
                    .build();
        }
        return null;
    }

    // 일정 최소 정보 조회
    private NotificationResp.ScheduleMinimalInfo fetchScheduleInfo(Long notificationIdx) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;
        QPersonalSchedule personalSchedule = QPersonalSchedule.personalSchedule;

        Tuple result = queryFactory
                .select(
                        personalSchedule.idx,
                        personalSchedule.title
                )
                .from(notification)
                .join(notification.personalSchedule, personalSchedule)
                .where(notification.idx.eq(notificationIdx))
                .fetchOne();

        if (result != null) {
            return NotificationResp.ScheduleMinimalInfo.builder()
                    .idx(result.get(personalSchedule.idx))
                    .title(result.get(personalSchedule.title))
                    .build();
        }
        return null;
    }

    // 이벤트 알림 메시지 생성
    private String buildEventMessage(String eventTitle, String notificationType) {
        if (eventTitle == null) {
            eventTitle = "이벤트";
        }

        switch (notificationType) {
            case "BEFORE_10MIN":
                return String.format("'%s' 공연/전시가 10분 후에 시작합니다.", eventTitle);
            case "BEFORE_1HOUR":
                return String.format("'%s' 공연/전시가 1시간 후에 시작합니다.", eventTitle);
            case "DAY_9AM":
                return String.format("오늘은 '%s' 공연/전시 시작일입니다.", eventTitle);
            case "CUSTOM_MESSAGE":
                return String.format("'%s' 관련 소식이 있습니다.", eventTitle);
            default:
                return "알림이 도착했습니다.";
        }
    }

    // 개인 일정 알림 메시지 생성
    private String buildScheduleMessage(String scheduleTitle, String notificationType) {
        if (scheduleTitle == null) {
            scheduleTitle = "일정";
        }

        switch (notificationType) {
            case "BEFORE_10MIN":
                return String.format("'%s' 일정이 10분 후에 시작합니다.", scheduleTitle);
            case "BEFORE_1HOUR":
                return String.format("'%s' 일정이 1시간 후에 시작합니다.", scheduleTitle);
            case "DAY_9AM":
                return String.format("오늘은 '%s' 일정이 있는 날입니다.", scheduleTitle);
            case "CUSTOM_MESSAGE":
                return String.format("'%s' 일정 관련 소식이 있습니다.", scheduleTitle);
            default:
                return String.format("'%s' 일정 알림이 도착했습니다.", scheduleTitle);
        }
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        QScheduleNotification notification = QScheduleNotification.scheduleNotification;

        // 단일 쿼리로 모든 읽지 않은 알림 업데이트
        queryFactory
                .update(notification)
                .set(notification.isRead, true)
                .where(notification.user.idx.eq(userId)
                        .and(notification.isRead.eq(false)))
                .execute();
    }

  @Override
  public List<NotificationResp> findAvailableNotifications(Long userIdx) {
    QScheduleNotification notification = QScheduleNotification.scheduleNotification;
    QUser user = QUser.user;
    QEventsInterest eventsInterest = QEventsInterest.eventsInterest;
    QEvents events = QEvents.events;
    QPersonalSchedule personalSchedule = QPersonalSchedule.personalSchedule;

    return queryFactory
        .select(
            Projections.constructor(
                NotificationResp.class,
                notification.idx,
                notification.user.idx,
                // 알림 유형에 따라 제목 생성
                new CaseBuilder()
                    .when(notification.scheduleType.eq(ScheduleType.EVENTS_INTEREST))
                    .then("공연/전시 알림")
                    .when(notification.scheduleType.eq(ScheduleType.PERSONAL_SCHEDULE))
                    .then("개인 일정 알림")
                    .otherwise("알림"),
                // 메시지 필드는 애플리케이션 레벨에서 처리(복잡한 로직)
                Expressions.asString("알림이 도착했습니다."), // 기본값, 후처리에서 수정
                notification.notificationTime,
                notification.isRead,
                notification.notificationType.stringValue(),
                notification.scheduleType.stringValue(),
                // formattedTime 필드는 후처리에서 계산
                Expressions.asString("")
            )
        )
        .from(notification)
        .leftJoin(notification.user, user)
        .leftJoin(notification.eventsInterest, eventsInterest)
        .leftJoin(eventsInterest.events, events)
        .leftJoin(notification.personalSchedule, personalSchedule)
        .where(notification.user.idx.eq(userIdx)
            .and(notification.isVisible.eq(true))
            .and(notification.notificationTime.loe(LocalDateTime.now())))
        .orderBy(notification.notificationTime.desc())
        .fetch()
        .stream()
        .map(this::enrichNotification) // 후처리로 메시지와 formattedTime 설정
        .collect(Collectors.toList());
  }
}