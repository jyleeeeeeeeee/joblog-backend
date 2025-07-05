package com.joblog.user.dto;

import com.joblog.post.domain.Post;

import java.time.LocalDateTime;

public record MyPostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public MyPostResponse(Post post) {
        this(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt());
    }
}
