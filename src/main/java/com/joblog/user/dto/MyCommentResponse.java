package com.joblog.user.dto;

import com.joblog.comment.domain.Comment;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String content,
        Long postId,
        LocalDateTime createdAt
) {
    public MyCommentResponse(Comment comment) {
        this(comment.getId(), comment.getContent(), comment.getPost().getId(), comment.getCreatedAt());
    }
}
