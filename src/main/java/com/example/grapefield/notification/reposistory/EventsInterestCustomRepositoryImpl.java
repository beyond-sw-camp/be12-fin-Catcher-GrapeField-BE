package com.example.grapefield.notification.reposistory;

import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.events.model.entity.QTicketInfo;
import com.example.grapefield.events.model.response.EventsDetailCalendarListResp;
import com.example.grapefield.notification.model.entity.QEventsInterest;
import com.example.grapefield.notification.model.response.EventInterestCalendarResp;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventsInterestCustomRepositoryImpl implements EventsInterestCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<EventInterestCalendarResp> findMyInterestedEventsBetween(Long userIdx, LocalDateTime start, LocalDateTime end) {
    QEvents events = QEvents.events;
    QTicketInfo ticketInfo = QTicketInfo.ticketInfo;
    QEventsInterest interest = QEventsInterest.eventsInterest;

    BooleanBuilder where = new BooleanBuilder();
    where.and(ticketInfo.saleStart.between(start, end));
    where.and(interest.user.idx.eq(userIdx));
    where.and(interest.isCalendar.isTrue()); // 즐겨찾기 or 알림 설정된 항목

    return queryFactory
        .select(Projections.constructor(EventInterestCalendarResp.class,
            events.idx,
            events.title,
            events.category,
            ticketInfo.saleStart,
            ticketInfo.saleEnd,
            ticketInfo.ticketVendor,
            ticketInfo.isPresale,
            events.venue,
            events.startDate,
            events.endDate,
            ticketInfo.ticketLink,
            interest.idx))
        .from(interest)
        .join(interest.events, events)
        .join(ticketInfo).on(ticketInfo.events.eq(events))
        .where(where)
        .orderBy(ticketInfo.saleStart.asc())
        .fetch();
  }
}
