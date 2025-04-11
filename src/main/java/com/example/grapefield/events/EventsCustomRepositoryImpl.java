package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.*;
import com.example.grapefield.events.model.response.EventsCalendarListResp;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventsCustomRepositoryImpl implements EventsCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<EventsCalendarListResp> findEventsBySaleStartBetween(LocalDateTime startDate, LocalDateTime endDate) {

    QEvents events = QEvents.events;
    QTicketInfo ticketInfo = QTicketInfo.ticketInfo;

    BooleanBuilder whereBuilder = new BooleanBuilder();

    // 예매 시작일이 해당 기간 내에 있는 경우
    whereBuilder.and(ticketInfo.saleStart.between(startDate, endDate));

    return queryFactory
        .select(Projections.constructor(EventsCalendarListResp.class,
            events.idx,
            events.title,
            events.category,
            ticketInfo.saleStart,
            ticketInfo.saleEnd,
            ticketInfo.ticketVendor,
            ticketInfo.isPresale))
        .from(events)
        .join(ticketInfo).on(ticketInfo.events.eq(events))
        .where(whereBuilder)
        .orderBy(ticketInfo.saleStart.asc())
        .fetch();
  }

  @Override
  public List<EventsCalendarListResp> findEventsBySaleEndBetween(LocalDateTime startDate, LocalDateTime endDate){
    QEvents events = QEvents.events;
    QTicketInfo ticketInfo = QTicketInfo.ticketInfo;

    BooleanBuilder whereBuilder = new BooleanBuilder();

    // 예매 종료일이 해당 기간 내에 있는 경우
    whereBuilder.and(ticketInfo.saleEnd.between(startDate, endDate));

    return queryFactory
        .select(Projections.constructor(EventsCalendarListResp.class,
            events.idx,
            events.title,
            events.category,
            ticketInfo.saleStart,
            ticketInfo.saleEnd,
            ticketInfo.ticketVendor,
            ticketInfo.isPresale))
        .from(events)
        .join(ticketInfo).on(ticketInfo.events.eq(events))
        .where(whereBuilder)
        .orderBy(ticketInfo.saleStart.asc())
        .fetch();
  }
}