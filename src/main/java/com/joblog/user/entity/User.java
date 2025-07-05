package com.joblog.user.entity;

import com.joblog.comment.domain.Comment;
import com.joblog.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment 방식
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // 중복 방지
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER) // roles 필드는 별도 테이블에 저장되며 즉시 로딩됨
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Post> posts = new ArrayList<>();

    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setUser(this);
    }

    public static User of(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
    }

    // JWT 생성 시 claim에 roles 포함하려면 필요함
    public List<String> getRoles() {
        return Collections.singletonList("ROLE_USER");
    }
}
