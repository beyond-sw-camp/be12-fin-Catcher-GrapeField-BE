package com.example.grapefield.events.repository;

import com.example.grapefield.events.model.entity.*;
import com.example.grapefield.events.model.response.*;
import com.example.grapefield.events.participant.model.entity.*;
import com.example.grapefield.events.participant.model.response.OrganizationListResp;
import com.example.grapefield.events.participant.model.response.PerformerListResp;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.QEventsInterest;
import com.example.grapefield.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
  public List<EventsDetailCalendarListResp> findDetailEventsBySaleStartBetween(LocalDateTime startDate, LocalDateTime endDate) {

    QEvents events = QEvents.events;
    QTicketInfo ticketInfo = QTicketInfo.ticketInfo;

    BooleanBuilder whereBuilder = new BooleanBuilder();

    // 예매 시작일이 해당 기간 내에 있는 경우
    whereBuilder.and(ticketInfo.saleStart.between(startDate, endDate));


    return queryFactory
            .select(Projections.constructor(EventsDetailCalendarListResp.class,
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
                    ticketInfo.ticketLink))
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
            .select(e, ei.count(), t)
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
            .select(e, ei.count(), t)
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
  public EventsDetailResp getEventDetail(Long eventsIdx, User user) {
    QEvents e = QEvents.events;
    QTicketInfo ti = QTicketInfo.ticketInfo;
    QSeatPrice sp = QSeatPrice.seatPrice;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    // 이벤트 기본 정보 조회
    Events event = queryFactory
        .selectFrom(e)
        .where(e.idx.eq(eventsIdx))
        .fetchOne();

    if (event == null) {
      return null; // 또는 예외 처리
    }

    // 사용자 맞춤 정보 조회 (isFavorite, isNotify), user가 null이어도 에러 방지
    boolean isFavorite = false;
    boolean isNotify = false;
    
    if (user != null) {
      EventsInterest interest = queryFactory
          .selectFrom(ei)
          .where(ei.user.eq(user)
              .and(ei.events.eq(event)))
          .fetchOne();

      isFavorite = interest != null && Boolean.TRUE.equals(interest.getIsFavorite());
      isNotify = interest != null && Boolean.TRUE.equals(interest.getIsNotify());
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
        .idx(eventsIdx)
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
        .isFavorite(isFavorite)
        .isNotify(isNotify)
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

  @Override
  public Page<EventsListResp> findEventsByKeyword(String keyword, Pageable pageable, User user) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");
    BooleanBuilder builder = new BooleanBuilder();

    if (!isAdmin) {
      builder.and(e.isVisible.isTrue()); // 일반 유저만 가시성 필터링
    }

    if (keyword != null && !keyword.isBlank()) {
      builder.and(
              e.title.containsIgnoreCase(keyword)
                      .or(e.venue.containsIgnoreCase(keyword))
                      .or(e.category.stringValue().containsIgnoreCase(keyword))
                      .or(e.description.containsIgnoreCase(keyword))
      );
    }
    // 데이터 조회
    List<Tuple> tuples = queryFactory
            .select(e, ei.count())
            .from(e)
            .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
            .where(builder)
            .groupBy(e)
            .orderBy(e.startDate.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // 총 개수 조회 쿼리
    Long totalCount = queryFactory.select(e.count()).from(e).where(builder).fetchOne();
    long total = totalCount != null ? totalCount : 0L;

    return toPage(tuples, pageable, total);
  }

  private Page<EventsListResp> toPage(List<Tuple> tuples, Pageable pageable, long total) {
    List<EventsListResp> result = tuples.stream()
            .map(tuple -> {
              Events event = tuple.get(0, Events.class);
              Long interestCount = tuple.get(1, Long.class);

              return EventsListResp.builder()
                      .idx(event.getIdx())
                      .title(event.getTitle())
                      .category(event.getCategory())
                      .startDate(event.getStartDate())
                      .endDate(event.getEndDate())
                      .venue(event.getVenue())
                      .posterImgUrl(event.getPosterImgUrl())
                      .interestCtn(interestCount != null ? interestCount.intValue() : 0)
                      .isVisible(event.getIsVisible())
                      .build();
            })
            .toList();

    return new PageImpl<>(result, pageable, total);
  }


  @Override
  public Page<EventsListResp> findEventsByKeywordAnd(List<String> keywords, Pageable pageable, User user) {
    QEvents e = QEvents.events;
    QEventsInterest ei = QEventsInterest.eventsInterest;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");
    BooleanBuilder builder = new BooleanBuilder();

    if (!isAdmin) {
      builder.and(e.isVisible.isTrue()); // 일반 유저만 가시성 필터링
    }

    if (keywords != null && !keywords.isEmpty()) {
      for (String keyword : keywords) {
        builder.and(
            e.title.containsIgnoreCase(keyword)
                .or(e.venue.containsIgnoreCase(keyword))
                .or(e.category.stringValue().containsIgnoreCase(keyword))
                .or(e.description.containsIgnoreCase(keyword))
        );
      }
    }

    List<Tuple> tuples = queryFactory
        .select(e, ei.count())
        .from(e)
        .leftJoin(ei).on(ei.events.eq(e).and(ei.isFavorite.isTrue()))
        .where(builder)
        .groupBy(e)
        .orderBy(e.startDate.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 총 개수 조회 쿼리
    Long totalCount = queryFactory.select(e.count()).from(e).where(builder).fetchOne();
    long total = totalCount != null ? totalCount : 0L;

    return toPage(tuples, pageable, total);
  }
}