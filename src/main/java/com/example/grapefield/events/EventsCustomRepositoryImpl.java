package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.*;
import com.example.grapefield.events.model.response.*;
import com.example.grapefield.events.participant.model.entity.*;
import com.example.grapefield.events.participant.model.response.OrganizationListResp;
import com.example.grapefield.events.participant.model.response.PerformerListResp;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    if (category != null && category != EventCategory.ALL) {
      builder.and(e.category.eq(category));
    }
    builder.and(e.endDate.goe(now)); //종료일이 현재 이후인 공연

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
    if (category != EventCategory.ALL) {
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
    if (category != EventCategory.ALL) {
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

  private Slice<EventsTicketScheduleListResp> toScheduleSlice(List<Tuple> tuples, Pageable pageable) {
    QEvents e = QEvents.events;
    QTicketInfo t = QTicketInfo.ticketInfo;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    List<EventsTicketScheduleListResp> result = tuples.stream()
            .map(tuple -> EventsTicketScheduleListResp.from(
                    tuple.get(e),
                    tuple.get(t),
                    tuple.get(ei.count())
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
  public Slice<EventsTicketScheduleListResp> findEventsWithUpcomingTicketOpenings(LocalDateTime now, Pageable pageable) {
    QEvents e = QEvents.events;
    QTicketInfo t = QTicketInfo.ticketInfo;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    LocalDateTime sevenDaysLater = now.plusDays(7);

    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(t)
            .join(t.events, e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(
                    t.saleStart.gt(now)
                            .and(t.saleStart.loe(sevenDaysLater)) // 7일 이내
            )
            .groupBy(e)
            .orderBy(t.saleStart.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

    return toScheduleSlice(tuples, pageable);
  }

  @Override
  public Slice<EventsTicketScheduleListResp> findEventsWithUpcomingTicketClosures(LocalDateTime now, Pageable pageable) {
    QEvents e = QEvents.events;
    QTicketInfo t = QTicketInfo.ticketInfo;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    LocalDateTime sevenDaysLater = now.plusDays(7);

    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(t)
            .join(t.events, e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(
                    t.saleStart.loe(now) // 이미 오픈된 공연
                            .and(t.saleEnd.gt(now)) // 마감되지 않았고
                            .and(t.saleEnd.loe(sevenDaysLater)) // 7일 이내 종료될 예정
            )
            .groupBy(e)
            .orderBy(t.saleEnd.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

    return toScheduleSlice(tuples, pageable);
  }

  @Override
  public EventsDetailResp getEventDetail(Long eventsIdx) {
    QEvents e = QEvents.events;
    QTicketInfo ti = QTicketInfo.ticketInfo;
    QSeatPrice sp = QSeatPrice.seatPrice;

    // 이벤트 기본 정보 조회
    Events event = queryFactory
        .selectFrom(e)
        .where(e.idx.eq(eventsIdx))
        .fetchOne();

    if (event == null) {
      return null; // 또는 예외 처리
    }

    // 별도 쿼리로 티켓 정보 조회 (N+1 방지를 위해 in 절 사용)
    List<TicketInfo> ticketInfos = queryFactory
        .selectFrom(ti)
        .where(ti.events.idx.eq(eventsIdx))
        .fetch();

    // 별도 쿼리로 좌석 가격 정보 조회 (N+1 방지를 위해 in 절 사용)
    List<SeatPrice> seatPrices = queryFactory
        .selectFrom(sp)
        .where(sp.events.idx.eq(eventsIdx))
        .fetch();

    // DTO 변환 및 반환
    return EventsDetailResp.builder()
        .title(event.getTitle())
        .category(event.getCategory())
        .startDate(event.getStartDate())
        .endDate(event.getEndDate())
        .posterImgUrl(event.getPosterImgUrl())
        .description(event.getDescription())
        .notification(event.getNotification())
        .venue(event.getVenue())
        .runningTime(event.getRunningTime())
        .ageLimit(event.getAgeLimit())
        .ticketInfoList(ticketInfos.stream()
            .map(this::convertToTicketInfoDetailResp)
            .toList())
        .seatPriceList(seatPrices.stream()
            .map(this::convertToSeatPriceDetailResp)
            .toList())
        .build();
  }

  //TicketInfo 엔티티를 TicketInfoDetailResp DTO로 변환
  private TicketInfoDetailResp convertToTicketInfoDetailResp(TicketInfo ticketInfo) {
    return new TicketInfoDetailResp(
        ticketInfo.getTicketLink(),
        ticketInfo.getIsPresale(),
        ticketInfo.getSaleStart(),
        ticketInfo.getSaleEnd(),
        ticketInfo.getTicketVendor()
    );
  }

  //SeatPrice 엔티티를 SeatPriceDetailResp DTO로 변환
  private SeatPriceDetailResp convertToSeatPriceDetailResp(SeatPrice seatPrice) {
    return new SeatPriceDetailResp(
        seatPrice.getSeatType(),
        seatPrice.getPrice(),
        seatPrice.getDescription()
    );
  }

  @Override
  public Map<String, Object> getParticipantDetail(Long eventsIdx) {
    QEventsCast ec = QEventsCast.eventsCast;
    QPerformer p = QPerformer.performer;
    QEventsParticipation ep = QEventsParticipation.eventsParticipation;
    QOrganization o = QOrganization.organization;

    // 출연진 정보 조회 (fetch join으로 N+1 문제 해결)
    List<EventsCast> eventsCasts = queryFactory
        .selectFrom(ec)
        .join(ec.performer, p).fetchJoin()
        .where(ec.events.idx.eq(eventsIdx))
        .orderBy(ec.castOrder.asc())
        .fetch();

    List<PerformerListResp> performers = eventsCasts.stream()
        .map(cast -> PerformerListResp.builder()
            .idx(cast.getPerformer().getIdx())
            .name(cast.getPerformer().getName())
            .imgUrl(cast.getPerformer().getImgUrl())
            .role(cast.getRole())
            .build())
        .collect(Collectors.toList());

    // 참여 기관 정보 조회 (fetch join으로 N+1 문제 해결)
    List<EventsParticipation> participations = queryFactory
        .selectFrom(ep)
        .join(ep.organization, o).fetchJoin()
        .where(ep.events.idx.eq(eventsIdx))
        .fetch();

    List<OrganizationListResp> organizations = participations.stream()
        .map(participation -> OrganizationListResp.builder()
            .idx(participation.getOrganization().getIdx())
            .name(participation.getOrganization().getName())
            .imgUrl(participation.getOrganization().getImgUrl())
            .type(participation.getAssociationType())
            .build())
        .collect(Collectors.toList());

    // 결과 Map 생성
    Map<String, Object> result = new HashMap<>();
    result.put("performers", performers);
    result.put("organizations", organizations);

    return result;
  }
}