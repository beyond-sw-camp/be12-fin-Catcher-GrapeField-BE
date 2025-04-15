package com.example.grapefield.events.post;

import com.example.grapefield.events.post.model.entity.QPost;
import com.example.grapefield.events.post.model.entity.QPostRecommend;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
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
  public Page<PostListResp> findPostList(Long boardIdx, Pageable pageable) {
    // 일반 사용자는 보이는 게시물만 조회할 수 있음
    return getPostListWithCondition(boardIdx, pageable, true);
  }

  @Override
  public Page<PostListResp> findPostListForAdmin(Long boardIdx, Pageable pageable) {
    // 관리자는 모든 게시물을 조회할 수 있도록 isVisible 조건 제거
    return getPostListWithCondition(boardIdx, pageable, false);
  }

  private Page<PostListResp> getPostListWithCondition(Long boardIdx, Pageable pageable, boolean checkVisibility) {
    QPost post = QPost.post;
    QUser qUser = QUser.user;
    QPostRecommend recommend = QPostRecommend.postRecommend;

    // 쿼리 생성 시작
    JPAQuery<PostListResp> query = queryFactory
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
        .where(post.board.idx.eq(boardIdx));

    // checkVisibility가 true인 경우에만 isVisible 조건 추가
    if (checkVisibility) {
      query = query.where(post.isVisible.isTrue());
    }

    // 나머지 쿼리 조건 추가
    List<PostListResp> results = query
        .groupBy(post.idx, qUser.username, post.title, post.viewCnt, post.postType,
            post.createdAt, post.isVisible)
        .orderBy(post.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 카운트 쿼리
    JPAQuery<Long> countQuery = queryFactory
        .select(post.count())
        .from(post)
        .where(post.board.idx.eq(boardIdx));

    // checkVisibility가 true인 경우에만 isVisible 조건 추가
    if (checkVisibility) {
      countQuery = countQuery.where(post.isVisible.isTrue());
    }

    return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
  }
}