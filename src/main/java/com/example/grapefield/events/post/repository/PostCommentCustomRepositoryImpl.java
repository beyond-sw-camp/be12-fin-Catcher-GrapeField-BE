package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.QPost;
import com.example.grapefield.events.post.model.entity.QPostComment;
import com.example.grapefield.events.post.model.response.CommentListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.QUser;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
public class PostCommentCustomRepositoryImpl implements PostCommentCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CommentListResp> findCommentList(Long postIdx, Pageable pageable, User user) {
    QPostComment qPostComment = QPostComment.postComment;
    QUser qUser = QUser.user;
    QPost qPost = QPost.post;

    // 댓글 리스트
    List<CommentListResp> comments = queryFactory
        .select(Projections.constructor(CommentListResp.class,
            qPostComment.idx,
            qPostComment.user.idx,
            qUser.username,
            qPostComment.content,
            qPostComment.createdAt,
            qPostComment.updatedAt.isNotNull().and(qPostComment.createdAt.ne(qPostComment.updatedAt)),
            user == null
                ? Expressions.constant(false)
                : Expressions.booleanTemplate(
                "({0} = {1}) or ({2} = 'ROLE_ADMIN')",
                qPostComment.user.idx,
                user.getIdx(),
                user.getRole().name()
            )
        ))
        .from(qPostComment)
        .join(qPostComment.user, qUser)
        .join(qPostComment.post, qPost)
        .where(qPost.idx.eq(postIdx))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(qPostComment.createdAt.desc())
        .fetch();

    // 총 개수 쿼리
    Long total = queryFactory
        .select(qPostComment.count())
        .from(qPostComment)
        .join(qPostComment.post, qPost)
        .where(qPost.idx.eq(postIdx))
        .fetchOne();

    return new PageImpl<>(comments, pageable, total != null ? total : 0);
  }
}
