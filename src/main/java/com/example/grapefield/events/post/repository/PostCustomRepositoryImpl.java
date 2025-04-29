package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.QEvents;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.entity.*;
import com.example.grapefield.events.post.model.response.*;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CommunityPostListResp> findPostLists(User user, Pageable pageable, String type, String orderBy) {
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QPostRecommend recommend = QPostRecommend.postRecommend;
    QEvents events = QEvents.events;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder();

    // 문자열 type을 EventCategory로 변환
    if (type != null && !type.equals("ALL")) {
      try {
        EventCategory category = EventCategory.valueOf(type);
        builder.and(events.category.eq(category));
      } catch (IllegalArgumentException e) {
        // 잘못된 카테고리 값 처리
        System.out.println("Invalid category: " + type);
      }
    }

    if (!isAdmin) {
      builder.and(post.isVisible.isTrue());
    }

    List<CommunityPostListResp> results = queryFactory
            .select(Projections.constructor(CommunityPostListResp.class,
                    post.idx,
                    qUser.username,
                    post.title,
                    post.content,
                    post.viewCnt,
                    post.postType,
                    post.createdAt,
                    post.isVisible,
                    recommend.idx.count().intValue(),
                    events.idx,
                    events.title,
                    events.category
            ))
            .from(post)
            .join(post.user, qUser)
            .join(events).on(events.idx.eq(post.board.idx))
            .leftJoin(recommend).on(recommend.post.eq(post))
            .where(builder)
            .groupBy(post.idx, qUser.username, post.title, post.content, post.viewCnt, post.postType,
                    post.createdAt, post.isVisible, events.idx, events.title, events.category)
            .orderBy(getOrderSpecifier(orderBy, post))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    JPAQuery<Long> countQuery = queryFactory
            .select(post.count())
            .from(post)
            .join(events).on(events.idx.eq(post.board.idx))
            .where(builder);

    return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
  }

  // 정렬 기준에 따른 OrderSpecifier 반환 메서드
  private OrderSpecifier<?> getOrderSpecifier(String orderBy, QPost post) {
    if ("popular".equals(orderBy)) {
      return post.viewCnt.desc();
    } else {
      return post.createdAt.desc(); // 기본값: 최신순
    }
  }

  @Override
  public Page<PostListResp> findPostList(Long boardIdx, Pageable pageable, PostType postType, User user) {
    // 일반 사용자는 보이는 게시물만 조회할 수 있음
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QPostRecommend recommend = QPostRecommend.postRecommend;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    // where 조건 생성을 위한 BooleanBuilder 사용
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(post.board.idx.eq(boardIdx)); // 기본 조건: 게시판 ID 일치

    // PostType 필터링 조건 추가 (ALL이 아닌 경우에만)
    if (postType != null && postType != PostType.ALL) { builder.and(post.postType.eq(postType)); }

    if (!isAdmin) {
      builder.and(post.isVisible.isTrue()); // 일반 유저만 가시성 필터링
    }

    // 쿼리 생성
    List<PostListResp> results = queryFactory
            .select(Projections.constructor(PostListResp.class,
                    post.idx,
                    qUser.username,
                    post.title,
                    post.viewCnt,
                    post.postType,
                    post.createdAt,
                    post.isVisible,
                    recommend.idx.count().intValue()
            ))
            .from(post)
            .join(post.user, qUser)
            .leftJoin(recommend).on(recommend.post.eq(post))
            .where(builder) // 통합된 조건 적용
            .groupBy(post.idx, qUser.username, post.title, post.viewCnt, post.postType,
                    post.createdAt, post.isVisible)
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // 카운트 쿼리 - 동일한 조건 사용
    JPAQuery<Long> countQuery = queryFactory
            .select(post.count())
            .from(post)
            .where(builder); // 동일한 조건 적용

    return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
  }

  @Override
  public PostDetailResp findPostDetail(Long idx, User user) {
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QPostRecommend recommend = QPostRecommend.postRecommend;
    QPostAttachment attachment = QPostAttachment.postAttachment;

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(post.idx.eq(idx));

    boolean editable = false;
    if (user != null) {
      editable = user.getRole().equals(UserRole.ROLE_ADMIN) ||
          queryFactory.selectOne()
              .from(post)
              .where(post.idx.eq(idx).and(post.user.idx.eq(user.getIdx())))
              .fetchFirst() != null;
    }

    // 게시글 기본 정보 조회
    PostDetailResp result = queryFactory
        .select(Projections.constructor(PostDetailResp.class,
            post.idx,
            qUser.idx,
            qUser.username,
            post.title,
            post.content,
            post.viewCnt,
            post.postType,
            post.createdAt,
            recommend.idx.countDistinct().intValue(),
            Expressions.constant(editable)))
        .from(post)
        .join(post.user, qUser)
        .leftJoin(recommend).on(recommend.post.eq(post).and(recommend.isRecommended.isTrue()))
        .where(builder)
        .groupBy(post.idx, qUser.idx, qUser.username, post.title, post.content,
            post.viewCnt, post.postType, post.createdAt)
        .fetchOne();

    // 첨부파일 경로 리스트 추가
    if (result != null) {
      List<String> imagePaths = queryFactory
          .select(attachment.fileUrl)
          .from(attachment)
          .where(attachment.post.idx.eq(idx))
          .fetch();

      result = PostDetailResp.builder()
          .idx(result.getIdx())
          .user_idx(result.getUser_idx())
          .writer(result.getWriter())
          .title(result.getTitle())
          .content(result.getContent())
          .viewCnt(result.getViewCnt())
          .postType(result.getPostType())
          .createdAt(result.getCreatedAt())
          .recommendCnt(result.getRecommendCnt())
          .editable(result.isEditable())
          .images(imagePaths)
          .build();
    }

    return result;
  }

  // 구현체
  @Override
  public Page<UserPostListResp> postsFindByUserIdx(Long userIdx, Pageable pageable) {
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QEvents events = QEvents.events;
    QPostComment postComment = QPostComment.postComment;

    // 전체 게시물 수를 조회
    long total = queryFactory
            .select(post.count())
            .from(post)
            .join(post.user, qUser)
            .join(events).on(events.idx.eq(post.board.idx))
            .where(post.user.idx.eq(userIdx))
            .fetchOne();

    // 페이지네이션을 적용하여 게시물 목록을 조회
    List<UserPostListResp> content = queryFactory
            .select(Projections.constructor(UserPostListResp.class,
                    post.idx,
                    post.title,
                    post.createdAt,
                    events.idx,
                    events.title,
                    events.category,
                    JPAExpressions.select(postComment.count())
                            .from(postComment)
                            .where(postComment.post.eq(post))))
            .from(post)
            .join(post.user, qUser)
            .join(events).on(events.idx.eq(post.board.idx))
            .where(post.user.idx.eq(userIdx))
            .orderBy(post.createdAt.desc()) // 최신순 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<UserCommentListResp> commentsFindByUserIdx(Long userIdx, Pageable pageable) {
    QPostComment comment = QPostComment.postComment;
    QUser user = QUser.user;
    QPost post = QPost.post;
    QEvents events = QEvents.events;

    // 총 개수 조회
    long total = queryFactory
            .select(comment.count())
            .from(comment)
            .join(comment.user, user)
            .join(comment.post, post)
            .join(events).on(events.idx.eq(post.board.idx))
            .where(user.idx.eq(userIdx))
            .fetchOne();

    // 데이터 조회
    List<UserCommentListResp> content = queryFactory
            .select(Projections.constructor(UserCommentListResp.class,
                    comment.idx,
                    comment.content,
                    post.idx,
                    post.title,
                    comment.createdAt,
                    events.idx,
                    events.title,
                    events.category))
            .from(comment)
            .join(comment.user, user)
            .join(comment.post, post)
            .join(events).on(events.idx.eq(post.board.idx))
            .where(user.idx.eq(userIdx))
            .orderBy(comment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    return new PageImpl<>(content, pageable, total);
  }

  private Page<PostSearchListResp> toPagePostListResp(List<Tuple> tuples, Pageable pageable, long total) {
    List<PostSearchListResp> result = tuples.stream()
            .map(tuple -> PostSearchListResp.builder()
                    .idx(tuple.get(0, Long.class))
                    .writer(tuple.get(1, String.class))
                    .title(tuple.get(2, String.class))
                    .viewCnt(tuple.get(3, Integer.class))
                    .postType(tuple.get(4, PostType.class))
                    .createdAt(tuple.get(5, LocalDateTime.class))
                    .isVisible(tuple.get(6, Boolean.class))
                    .boardIdx(tuple.get(7, Long.class))
                    .boardTitle(tuple.get(8, String.class))
                    .build()
            ).toList();

    return new PageImpl<>(result, pageable, total);
  }

  @Override
  public Page<PostSearchListResp> findPostsByKeyword(String keyword, Pageable pageable, User user) {
    QPost p = QPost.post;
    QUser u = QUser.user;
    QBoard b = QBoard.board;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder();

    if (keyword != null && !keyword.isBlank()) {
      builder.and(
              p.title.containsIgnoreCase(keyword)
                      .or(p.content.containsIgnoreCase(keyword))
      );
    }

    if (!isAdmin) { builder.and(p.isVisible.isTrue()); }

    List<Tuple> tuples = queryFactory
            .select(p.idx, u.username, p.title, p.viewCnt, p.postType, p.createdAt, p.isVisible, b.idx, b.title)
            .from(p)
            .join(p.user, u)
            .leftJoin(p.board, b)
            .where(builder)
            .orderBy(p.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long totalCount = queryFactory
            .select(p.count())
            .from(p)
            .where(builder)
            .fetchOne();

    long total = totalCount != null ? totalCount : 0L;

    return toPagePostListResp(tuples, pageable, total);
  }


  @Override
  public Page<PostSearchListResp> findPostsByKeywordAnd(List<String> keywords, Pageable pageable, User user) {
    QPost p = QPost.post;
    QUser u = QUser.user;
    QBoard b = QBoard.board;

    boolean isAdmin = user != null && user.getRole().name().equals("ROLE_ADMIN");

    BooleanBuilder builder = new BooleanBuilder();

    if (keywords != null && !keywords.isEmpty()) {
      for (String keyword : keywords) {
        builder.and(
                p.title.containsIgnoreCase(keyword)
                        .or(p.content.containsIgnoreCase(keyword))
        );
      }
    }

    if (!isAdmin) { builder.and(p.isVisible.isTrue()); }

    List<Tuple> tuples = queryFactory
            .select(p.idx, u.username, p.title, p.viewCnt, p.postType, p.createdAt, p.isVisible, b.idx, b.title)
            .from(p)
            .join(p.user, u)
            .leftJoin(p.board, b)
            .where(builder)
            .orderBy(p.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long totalCount = queryFactory
            .select(p.count())
            .from(p)
            .where(builder)
            .fetchOne();

    long total = totalCount != null ? totalCount : 0L;

    return toPagePostListResp(tuples, pageable, total);
  }


}