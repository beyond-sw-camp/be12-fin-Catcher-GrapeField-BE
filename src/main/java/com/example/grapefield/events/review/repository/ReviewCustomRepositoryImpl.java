package com.example.grapefield.events.review.repository;

import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.events.review.model.entity.QReview;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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

  @Override
  public Slice<ReviewListResp> findReviewsByKeyword(String keyword, Pageable pageable) {
    QReview qReview = QReview.review;
    QUser qUser = QUser.user;
    QEvents qEvents = QEvents.events;

    BooleanBuilder builder = new BooleanBuilder();

    if (keyword != null && !keyword.isBlank()) {
      builder.and(qReview.content.containsIgnoreCase(keyword));
    }

    List<ReviewListResp> reviews = queryFactory
        .select(Projections.constructor(
            ReviewListResp.class,
            qReview.idx,
            qReview.user.idx,
            qUser.username,
            qReview.rating,
            qReview.content,
            qReview.createdAt,
            Expressions.constant(false) // isOwner 기본 false (간편 모드)
        ))
        .from(qReview)
        .join(qReview.user, qUser)
        .join(qReview.events, qEvents)
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1) // ✅ Slice 구조
        .orderBy(qReview.createdAt.desc())
        .fetch();

    boolean hasNext = reviews.size() > pageable.getPageSize();
    if (hasNext) {
      reviews.remove(pageable.getPageSize()); // 마지막 항목 잘라내기
    }

    return new SliceImpl<>(reviews, pageable, hasNext);
  }


  @Override
  public Slice<ReviewListResp> findReviewsByKeywordAnd(List<String> keywords, Pageable pageable) {
    return null;
  }
}
