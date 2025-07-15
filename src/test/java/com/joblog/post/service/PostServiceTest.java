package com.joblog.post.service;

import com.joblog.post.domain.Post;
import com.joblog.post.dto.PostRequest;
import com.joblog.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글 생성에 성공한다")
    void createPost_success() {
//        // given
//        PostRequest request = new PostRequest(
//                "제목입니다",
//                "내용입니다"
//        );

        // when
//        Long postId = postService.create(request, "test@example.com");
//
//        // then
//        Post post = postRepository.findById(postId).orElseThrow();
//        assertThat(post.getTitle()).isEqualTo("제목입니다");
//        assertThat(post.getContent()).isEqualTo("내용입니다");
//        assertThat(post.getUser().getEmail()).isEqualTo("test@example.com");
    }

}