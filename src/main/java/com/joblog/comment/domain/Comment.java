package com.joblog.comment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joblog.common.entity.BaseEntity;
import com.joblog.post.domain.Post;
import com.joblog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.deleted = true;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
