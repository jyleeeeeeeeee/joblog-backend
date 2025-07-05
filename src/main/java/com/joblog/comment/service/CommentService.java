package com.joblog.comment.service;

import com.joblog.comment.domain.Comment;
import com.joblog.comment.dto.CommentRequest;
import com.joblog.comment.dto.CommentResponse;
import com.joblog.user.entity.User;

import java.util.List;

public interface CommentService {
    Comment create(User user, CommentRequest request);

    void update(User user, Long commentId, CommentRequest request);

    void delete(User user, Long commentId);

    List<CommentResponse> getCommentsByPost(Long postId);
}
