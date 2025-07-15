package com.joblog.common;

import com.joblog.comment.dto.CommentRequest;
import com.joblog.comment.dto.CommentResponse;
import com.joblog.comment.repository.CommentRepository;
import com.joblog.comment.service.CommentService;
import com.joblog.post.domain.Post;
import com.joblog.post.repository.PostRepository;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceIntegrationTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 댓글_작성_조회() {
        User user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("테스트유저")
                .build());

        Post post = postRepository.save(Post.builder()
                .title("title")
                .content("content")
                .user(user)
                .build());

        CommentRequest commentRequest = new CommentRequest(post.getId(), "댓글 내용");

        commentService.create(user, commentRequest);

        List<CommentResponse> comments = commentService.getCommentsByPost(post.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글 내용");
    }
}
