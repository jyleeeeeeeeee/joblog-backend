package com.joblog.postlike.domain;

import com.joblog.post.domain.Post;
import com.joblog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"}) // ✅ 유저-게시글 조합 유니크
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 좋아요 누른 게시글
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;
}
