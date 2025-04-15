package com.example.grapefield.events.post;

import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.entity.QPost;
import com.example.grapefield.events.post.model.entity.QPostRecommend;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<PostListResp> findPostList(Long boardIdx, Pageable pageable, PostType postType) {
    // 일반 사용자는 보이는 게시물만 조회할 수 있음
    return getPostListWithCondition(boardIdx, pageable, true, postType);
  }

  @Override
  public Page<PostListResp> findPostListForAdmin(Long boardIdx, Pageable pageable, PostType postType) {
    // 관리자는 모든 게시물을 조회할 수 있도록 isVisible 조건 제거
    return getPostListWithCondition(boardIdx, pageable, false, postType);
  }

  private Page<PostListResp> getPostListWithCondition(Long boardIdx, Pageable pageable, boolean checkVisibility, PostType postType) {
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QPostRecommend recommend = QPostRecommend.postRecommend;

    // where 조건 생성을 위한 BooleanBuilder 사용
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(post.board.idx.eq(boardIdx)); // 기본 조건: 게시판 ID 일치

    // 가시성 조건 추가
    if (checkVisibility) { builder.and(post.isVisible.isTrue()); }

    // PostType 필터링 조건 추가 (ALL이 아닌 경우에만)
    if (postType != null && postType != PostType.ALL) { builder.and(post.postType.eq(postType)); }

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
}