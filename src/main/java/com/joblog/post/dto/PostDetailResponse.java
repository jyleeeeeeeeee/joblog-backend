package com.joblog.post.dto;

import com.joblog.comment.domain.Comment;
import com.joblog.post.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class PostDetailResponse extends PostResponse {
    public PostDetailResponse(Post post) {
        super(post);
    }
//    private final Long id;
//    private final String title;
//    private final String content;
//    private final String authorEmail;
//    private final List<Comment> comments;
//    private int commentCount = 0;
//    private final List<FileAttachmentResponse> attachments;
//
//    public PostDetailResponse(Post post) {
//        this.id = post.getId();
//        this.title = post.getTitle();
//        this.content = post.getContent();
//        this.authorEmail = post.getUser().getEmail();
//        this.comments = post.getComments();
//        this.commentCount = comments.size();
//        this.attachments = post.getAttachments().stream().map(FileAttachmentResponse::new).toList();
//    }

}
