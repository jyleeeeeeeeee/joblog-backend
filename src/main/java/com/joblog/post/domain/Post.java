package com.joblog.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joblog.comment.domain.Comment;
import com.joblog.common.entity.BaseEntity;
import com.joblog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileAttachment> attachments = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    // 게시글 수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 게시글 삭제
    public void delete() {
        this.deleted = true;
        comments.forEach(Comment::delete);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void addAttachment(FileAttachment attachment) {
        attachments.add(attachment);
        attachment.setPost(this);
    }
}
