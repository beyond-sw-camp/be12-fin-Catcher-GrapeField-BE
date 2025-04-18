package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.entity.QPost;
import com.example.grapefield.events.post.model.entity.QPostAttachment;
import com.example.grapefield.events.post.model.entity.QPostRecommend;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
            recommend.idx.count().intValue(),
            Expressions.constant(editable)))
        .from(post)
        .join(post.user, qUser)
        .leftJoin(recommend).on(recommend.post.eq(post))
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

  @Override
  public Slice<PostListResp> findPostsByKeyword(String keyword, Pageable pageable) {
    QPost p = QPost.post;
    QUser u = QUser.user;
    QPostRecommend pr = QPostRecommend.postRecommend;

    BooleanBuilder builder = new BooleanBuilder();

    if (keyword != null && !keyword.isBlank()) {
      builder.and(
          p.title.containsIgnoreCase(keyword)
              .or(p.content.containsIgnoreCase(keyword))
      );
    }

    builder.and(p.isVisible.isTrue());

    List<Tuple> tuples = queryFactory
        .select(p, u.username, pr.count())
        .from(p)
        .join(p.user, u)
        .leftJoin(pr).on(pr.post.eq(p))
        .where(builder)
        .groupBy(p.idx, u.username)
        .orderBy(p.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1) // Slice 방식
        .fetch();

    return toSlicePostListResp(tuples, pageable);
  }

  private Slice<PostListResp> toSlicePostListResp(List<Tuple> tuples, Pageable pageable) {
    QPost p = QPost.post;
    QPostRecommend pr = QPostRecommend.postRecommend;
    QUser u = QUser.user;

    List<PostListResp> result = tuples.stream()
        .map(tuple -> PostListResp.builder()
            .idx(tuple.get(p.idx))
            .writer(tuple.get(u.username))
            .title(tuple.get(p.title))
            .viewCnt(tuple.get(p.viewCnt) != null ? tuple.get(p.viewCnt) : 0)
            .postType(tuple.get(p.postType))
            .createdAt(tuple.get(p.createdAt))
            .isVisible(tuple.get(p.isVisible))
            .recommendCnt(tuple.get(pr.count()) != null ? tuple.get(pr.count()).intValue() : 0)
            .build()
        ).toList();

    boolean hasNext = result.size() > pageable.getPageSize();
    return new SliceImpl<>(
        hasNext ? result.subList(0, pageable.getPageSize()) : result,
        pageable,
        hasNext
    );
  }


  @Override
  public Slice<PostListResp> findPostsByKeywordAnd(List<String> keywords, Pageable pageable) {
    QPost p = QPost.post;
    QUser u = QUser.user;
    QPostRecommend pr = QPostRecommend.postRecommend;

    BooleanBuilder builder = new BooleanBuilder();

    if (keywords != null && !keywords.isEmpty()) {
      for(String keyword:keywords){
        builder.and(
            p.title.containsIgnoreCase(keyword)
                .or(p.content.containsIgnoreCase(keyword))
        );
      }
    }

    builder.and(p.isVisible.isTrue());

    List<Tuple> tuples = queryFactory
        .select(p, u.username, pr.count())
        .from(p)
        .join(p.user, u)
        .leftJoin(pr).on(pr.post.eq(p))
        .where(builder)
        .groupBy(p.idx, u.username)
        .orderBy(p.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1) // Slice 방식
        .fetch();

    return toSlicePostListResp(tuples, pageable);
  }

}