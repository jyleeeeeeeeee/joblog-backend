package com.joblog.post.dto;

import com.joblog.comment.domain.Comment;
import com.joblog.post.domain.Post;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String authorEmail;
    private final List<Comment> comments;
    private int commentCount = 0;

    private List<FileAttachmentResponse> attachments;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorEmail = post.getUser().getEmail();
        this.comments = new ArrayList<>();
        this.attachments = post.getAttachments().stream().map(FileAttachmentResponse::new).toList();
    }

    public PostResponse(Post post, List<Comment> comments) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorEmail = post.getUser().getEmail();
        this.comments = comments;
        this.commentCount = comments.size();
    }

    public PostResponse(Long id, String title, String content, String authorEmail) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorEmail = authorEmail;
        this.comments = new ArrayList<>();
    }

}
