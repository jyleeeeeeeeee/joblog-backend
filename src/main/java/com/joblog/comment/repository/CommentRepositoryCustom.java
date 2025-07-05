package com.joblog.comment.repository;

import com.joblog.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Optional<Comment> findByCommentId(Long commentId);
    List<Comment> findAllByPostId(Long postId);
}
