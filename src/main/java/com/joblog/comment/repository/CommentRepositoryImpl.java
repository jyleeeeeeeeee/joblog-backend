package com.joblog.comment.repository;

import com.joblog.comment.domain.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.joblog.comment.domain.QComment.comment;
import static com.joblog.post.domain.QPost.post;
import static com.joblog.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Comment> findByCommentId(Long commentId) {
        return Optional.ofNullable(queryFactory.selectFrom(comment)
                .join(comment.user, user)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne());
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.post.id.eq(postId));
        builder.and(comment.deleted.eq(false));

        return queryFactory.selectFrom(comment)
                .join(comment.post, post).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(builder)
                .orderBy(comment.createBy.asc())
                .fetch();
    }
}
