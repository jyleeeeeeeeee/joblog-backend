package com.joblog.postlike.repository;

import com.joblog.postlike.domain.PostLike;
import com.joblog.postlike.domain.QPostLike;
import com.joblog.user.entity.QUser;
import com.joblog.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.joblog.post.domain.QPost.post;
import static com.joblog.postlike.domain.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostLike> findAllByUser(User user) {
        return jpaQueryFactory.selectFrom(postLike)
                .join(postLike.post, post).fetchJoin()
                .join(postLike.user, QUser.user)
                .where(post.deleted.eq(false))
                .fetch();
    }
}
