package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.*;
import com.example.grapefield.events.model.response.EventsCalendarListResp;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.notification.model.entity.QEventsInterest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

  @Override
  public Slice<EventsListResp> findAllOrdered(Pageable pageable) {
    return findEventsByCategory(null, pageable);
  }

  @Override
  public Slice<EventsListResp> findAllFilteredByCategory(EventCategory category, Pageable pageable) {
    return findEventsByCategory(category, pageable);
  }

  private Slice<EventsListResp> findEventsByCategory(EventCategory category, Pageable pageable) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;
    LocalDateTime now = LocalDateTime.now();

    List<Tuple> results = queryFactory
        .select(e, ei.count())
        .from(e)
        .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
        .where(
            category != null ? e.category.eq(category) : null
        )
        .groupBy(e)
        .orderBy(
            new CaseBuilder()
                .when(e.startDate.loe(now).and(e.endDate.goe(now))).then(0)
                .when(e.startDate.gt(now)).then(1)
                .otherwise(2).asc(),
            e.startDate.asc()
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1)
        .fetch();

    List<EventsListResp> content = results.stream()
        .map(tuple -> {
          Events event = tuple.get(e);
          Long count = tuple.get(ei.count());
          return EventsListResp.builder()
              .idx(event.getIdx())
              .title(event.getTitle())
              .category(event.getCategory())
              .startDate(event.getStartDate())
              .endDate(event.getEndDate())
              .posterImgUrl(event.getPosterImgUrl())
              .venue(event.getVenue())
              .interestCtn(count != null ? count.intValue() : 0)
              .build();
        })
        .toList();

    boolean hasNext = content.size() > pageable.getPageSize();
    return new SliceImpl<>(
        hasNext ? content.subList(0, pageable.getPageSize()) : content,
        pageable,
        hasNext
    );
  }

  private Slice<EventsListResp> toSlice(List<Tuple> tuples, Pageable pageable) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    List<EventsListResp> result = tuples.stream()
            .map(tuple -> EventsListResp.from(
                    tuple.get(e), tuple.get(ei.count())
            ))
            .toList();

    boolean hasNext = result.size() > pageable.getPageSize();
    return new SliceImpl<>(
            hasNext ? result.subList(0, pageable.getPageSize()) : result,
            pageable,
            hasNext
    );
  }


  @Override
  public Slice<EventsListResp> findTopRecommended(EventCategory category, LocalDateTime now, Pageable pageable) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    BooleanBuilder builder = new BooleanBuilder();
    if (category != null) {
      builder.and(e.category.eq(category));
    }
    builder.and(e.endDate.goe(now)); // 추천 조건

    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(builder)
            .groupBy(e)
            .orderBy(ei.count().desc(), e.startDate.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

    return toSlice(tuples, pageable);
  }

  @Override
  public Slice<EventsListResp> findTopPopular(EventCategory category, Pageable pageable) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    BooleanBuilder builder = new BooleanBuilder();
    if (category != null) {
      builder.and(e.category.eq(category));
    }

    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(builder)
            .groupBy(e)
            .orderBy(ei.count().desc(), e.startDate.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

    return toSlice(tuples, pageable);
  }

  @Override
  public Slice<EventsListResp> findTopUpcoming(EventCategory category, LocalDateTime now, Pageable pageable) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    BooleanBuilder builder = new BooleanBuilder();
    if (category != null) {
      builder.and(e.category.eq(category));
    }
    builder.and(e.startDate.gt(now));

    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(builder)
            .groupBy(e)
            .orderBy(e.startDate.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

    return toSlice(tuples, pageable);
  }
}