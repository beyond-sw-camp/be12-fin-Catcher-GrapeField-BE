package com.example.grapefield.events.review.repository;

import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.events.review.model.entity.QReview;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ReviewListResp> findReviewList(Long idx, Pageable pageable, Long rating, User user) {
    QReview qReview = QReview.review;
    QUser qUser = QUser.user;
    QEvents qEvents = QEvents.events;

    // rating이 null이 아닐 때만 rating 필터링 추가
    BooleanExpression ratingCondition = rating != null ? qReview.rating.eq(rating) : null;

    List<ReviewListResp> reviews = queryFactory
        .select(Projections.constructor(ReviewListResp.class,
            qReview.idx,
            qReview.user.idx,
            qUser.username,
            qReview.rating,
            qReview.content,
            qReview.createdAt,
            user == null
                ? Expressions.constant(false)
                : Expressions.booleanTemplate(
                "({0} = {1}) or ({2} = 'ROLE_ADMIN')",
                qReview.user.idx,
                user.getIdx(),
                user.getRole().name()
            )
        ))
        .from(qReview)
        .join(qReview.user, qUser)
        .join(qReview.events, qEvents)
        .where(qEvents.idx.eq(idx), ratingCondition)  // 여기서 동적 조건 추가
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(qReview.createdAt.desc())
        .fetch();

    // 총 개수 쿼리도 동일한 조건 적용
    Long total = queryFactory.select(qReview.count()).from(qReview).join(qReview.events, qEvents).where(qEvents.idx.eq(idx), ratingCondition).fetchOne();

    return new PageImpl<>(reviews, pageable, total != null ? total : 0);
  }
}
