package com.joblog.post.repository;

import com.joblog.post.domain.Post;
import com.joblog.post.domain.QFileAttachment;
import com.joblog.post.dto.PostSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.joblog.comment.domain.QComment.comment;
import static com.joblog.post.domain.QFileAttachment.fileAttachment;
import static com.joblog.post.domain.QPost.post;
import static com.joblog.postlike.domain.QPostLike.postLike;
import static com.joblog.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition.getTitle() != null && !condition.getTitle().isBlank()) {
            builder.and(post.title.containsIgnoreCase(condition.getTitle()));
        }
        if (condition.getAuthorEmail() != null && !condition.getAuthorEmail().isBlank()) {
            builder.and(user.email.eq(condition.getAuthorEmail()));
        }
        builder.and(post.deleted.eq(false));

//        // 좋아요 개수 계산용
//        NumberExpression<Long> likeCount = postLike.count();
//
//        // 정렬용 OrderSpecifier
//        List<OrderSpecifier<?>> orderSpecifiers = QuerydslUtil.getSortedColumn(pageable.getSort(), likeCount);


        // ✅ 실제 게시글 리스트 조회 쿼리
        JPAQuery<Post> query = queryFactory
                .selectFrom(post)                       // post 테이블로부터 select
                .join(post.user, user).fetchJoin()     // user 테이블을 join + 즉시 로딩(fetch join)
                .where(builder);

        for (Sort.Order order : pageable.getSort()) {
            PathBuilder<Post> entityPath = new PathBuilder<>(Post.class, post.getMetadata());

            // Comparable 타입으로 변환 (예: String, LocalDateTime, Integer 등)
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.getComparable(order.getProperty(), Comparable.class)
            );

            query.orderBy(orderSpecifier);
        }

        List<Post> content =
                query                        // 위에서 만든 조건들 적용
                        .offset(pageable.getOffset())          // 현재 페이지의 시작 위치
                        .limit(pageable.getPageSize())
                        .orderBy(post.createdAt.desc())        // 한 페이지에 조회할 게시글 수
                        .fetch();                              // 결과 리스트 반환

        // ✅ 전체 게시글 수 조회 (페이징의 totalElements 계산용)
        Long total = queryFactory
                .select(post.count())                  // 게시글 수 세기
                .from(post)                            // post 테이블로부터
                .join(post.user, user)
                .where(builder)                        // 동일한 조건으로 필터링
                .fetchOne();                           // 단일 값(Long) 조회

        // ✅ Page 객체 생성 후 반환 (content, pageable 정보, 전체 개수)
        return new PageImpl<>(content, pageable, total);

//        return PageableExecutionUtils.getPage(content, pageable,
//                () -> queryFactory.select(post.count()).from(post).where(builder).fetchOne());

    }

    @Override
    public Optional<Post> findPostForUpdate(Long postId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(post)
                .join(post.user, user)
                .fetchJoin()
                .where(post.id.eq(postId).and(post.deleted.eq(false)))
                .fetchOne());
    }

    @Override
    public Optional<Post> findPostForDelete(Long postId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .leftJoin(post.comments, comment).fetchJoin()
                .where(post.id.eq(postId).and(post.deleted.eq(false)))
                .distinct()
                .fetchOne());
    }

    @Override
    public Optional<Post> searchOne(Long postId) {
//        return Optional.ofNullable(queryFactory.selectFrom(post)
//                .join(post.comments, comment).fetchJoin()
//                .where(post.id.eq(postId).and(post.deleted.eq(false)))
//                .fetchOne());

        return Optional.ofNullable(
                queryFactory.selectFrom(post)
                .join(post.attachments, fileAttachment).fetchJoin()
                .where(post.id.eq(postId).and(post.deleted.eq(false)))
                .fetchOne());
    }
}

