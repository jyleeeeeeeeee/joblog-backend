package com.joblog.post.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.joblog.post.domain.QPost.post;

public class QuerydslUtil {

    public static List<OrderSpecifier<?>> getSortedColumn(Sort sort, NumberExpression<Long> likeCount) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            boolean isAsc = order.isAscending();

            switch (property) {
                case "likes" -> orders.add(isAsc ? likeCount.asc() : likeCount.desc());
                case "createdAt" -> orders.add(isAsc ? post.createdAt.asc() : post.createdAt.desc());
                default -> orders.add(post.createdAt.desc()); // 기본값
            }
        }

        return orders;
    }
}
