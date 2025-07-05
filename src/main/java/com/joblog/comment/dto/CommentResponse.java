package com.joblog.comment.dto;

import com.joblog.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private String authorEmail;
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorEmail = comment.getUser().getEmail();
        this.createdAt = comment.getCreatedAt();
    }
}
