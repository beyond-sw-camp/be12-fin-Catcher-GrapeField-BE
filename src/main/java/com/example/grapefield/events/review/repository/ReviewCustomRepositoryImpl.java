package com.example.grapefield.events.review.repository;

import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.review.model.entity.QReview;
import com.example.grapefield.events.review.model.entity.Review;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
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

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder()
            .and(qReview.events.idx.eq(idx));

    if (rating != null) {
      builder.and(qReview.rating.eq(rating));
    }

    if (!isAdmin) {
      builder.and(qReview.isVisible.isTrue()); // 일반 유저만 가시성 필터링
    }

    BooleanExpression editableExpr = getEditableExpr(user, qReview);

    List<ReviewListResp> reviews = queryFactory
            .select(Projections.constructor(ReviewListResp.class,
                    qReview.idx,
                    qReview.user.idx,
                    qUser.username,
                    qUser.profileImg,
                    qReview.rating,
                    qReview.content,
                    qReview.createdAt,
                    editableExpr,
                    qReview.isVisible
            ))
            .from(qReview)
            .join(qReview.user, qUser)
            .join(qReview.events, qEvents)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc())
            .fetch();

    Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .join(qReview.events, qEvents)
            .where(builder)
            .fetchOne();

    return new PageImpl<>(reviews, pageable, total != null ? total : 0);
  }

  private BooleanExpression getEditableExpr(User user, QReview qReview) {
    if (user == null) {
      return Expressions.asBoolean(Expressions.constant(false));
    }
    return Expressions.booleanTemplate(
            "({0} = {1}) or ({2} = 'ROLE_ADMIN')",
            qReview.user.idx,
            user.getIdx(),
            user.getRole().name()
    );
  }

  private Page<ReviewSearchList> toReviewSearchPage(List<Tuple> tuples, Pageable pageable, long total) {
    List<ReviewSearchList> results = tuples.stream()
            .map(tuple -> {
              QReview qReview = QReview.review;
              Review review = tuple.get(qReview);
              return ReviewSearchList.builder()
                      .idx(review.getIdx())
                      .user_idx(review.getUser().getIdx())
                      .writer(tuple.get(1, String.class))
                      .profileImg(tuple.get(2, String.class))
                      .rating(review.getRating())
                      .content(review.getContent())
                      .createdAt(review.getCreatedAt())
                      .editable(tuple.get(5, Boolean.class))
                      .isVisible(review.getIsVisible())
                      .boardIdx(tuple.get(3, Long.class)) // eventsIdx
                      .boardTitle(tuple.get(4, String.class)) // events.title
                      .build();
            })
            .toList();

    return new PageImpl<>(results, pageable, total);
  }

  @Override
  public Page<ReviewSearchList> findReviewsByKeyword(String keyword, Pageable pageable, User user) {
    QReview qReview = QReview.review;
    QUser qUser = QUser.user;
    QEvents qEvents = QEvents.events;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder();

    if (keyword != null && !keyword.isBlank()) {
      builder.and(
              qReview.content.containsIgnoreCase(keyword)
                      .or(qUser.username.containsIgnoreCase(keyword))
                      .or(qReview.events.title.containsIgnoreCase(keyword))
      );
    }

    if (!isAdmin) {
      builder.and(qReview.isVisible.isTrue());
    }

    BooleanExpression editableExpr = getEditableExpr(user, qReview);

    List<Tuple> results = queryFactory
            .select(qReview, qUser.username, qUser.profileImg, qReview.events.idx, qReview.events.title, editableExpr)
            .from(qReview)
            .join(qReview.user, qUser)
            .join(qReview.events, qEvents)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc())
            .fetch();

    Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .join(qReview.user, qUser)
            .join(qReview.events, qEvents)
            .where(builder)
            .fetchOne();

    return toReviewSearchPage(results, pageable, total != null ? total : 0);
  }

  @Override
  public Page<ReviewSearchList> findReviewsByKeywordAnd(List<String> keywords, Pageable pageable, User user) {
    QReview qReview = QReview.review;
    QUser qUser = QUser.user;
    QEvents qEvents = QEvents.events;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder();

    if (keywords != null && !keywords.isEmpty()) {
      for (String keyword : keywords) {
        BooleanBuilder keywordCondition = new BooleanBuilder();
        keywordCondition.or(qReview.content.containsIgnoreCase(keyword));
        keywordCondition.or(qUser.username.containsIgnoreCase(keyword));
        keywordCondition.or(qReview.events.title.containsIgnoreCase(keyword));
        builder.and(keywordCondition); // AND 조건
      }
    }

    if (!isAdmin) {
      builder.and(qReview.isVisible.isTrue());
    }

    BooleanExpression editableExpr = getEditableExpr(user, qReview);

    List<Tuple> results = queryFactory
            .select(qReview, qUser.username, qUser.profileImg, qReview.events.idx, qReview.events.title, editableExpr)
            .from(qReview)
            .join(qReview.user, qUser)
            .join(qReview.events, qEvents)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc())
            .fetch();

    Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .join(qReview.user, qUser)
            .join(qReview.events, qEvents)
            .where(builder)
            .fetchOne();

    return toReviewSearchPage(results, pageable, total != null ? total : 0);
  }
}
