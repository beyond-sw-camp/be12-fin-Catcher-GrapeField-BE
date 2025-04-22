package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.entity.QPersonalSchedule;
import com.example.grapefield.notification.model.response.PersonalScheduleResp;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonalScheduleCustomRepositoryImpl implements PersonalScheduleCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<PersonalScheduleResp> findPersonalSchedulesBetween(Long userIdx, LocalDateTime start, LocalDateTime end) {
    QPersonalSchedule p = QPersonalSchedule.personalSchedule;

    BooleanBuilder where = new BooleanBuilder();
    where.and(p.startDate.between(start, end));
    where.and(p.user.idx.eq(userIdx));

    return queryFactory
        .select(Projections.constructor(PersonalScheduleResp.class,
            p.idx,
            p.title,
            p.description,
            p.startDate,
            p.isNotify
        ))
        .from(p)
        .where(where)
        .orderBy(p.startDate.asc())
        .fetch();
  }
}
