package com.joblog.post;


import com.joblog.auth.jwt.JwtProvider;
import com.joblog.post.domain.FileAttachment;
import com.joblog.post.domain.Post;
import com.joblog.post.repository.PostRepository;
import com.joblog.postlike.repository.PostLikeRepository;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtProvider jwtProvider;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        postLikeRepository.deleteAll();

        postRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("1234")
                .nickname("테스트유저")
                .roles(Collections.singletonList("USER"))
                .build());

        // JWT 토큰 발급
        token = jwtProvider.createToken(user.getEmail(), user.getRoles());
    }

    @Test
    void test() throws Exception {
        // 테스트용 파일 생성 (PNG 이미지)
        MockMultipartFile image = new MockMultipartFile(
                "attachments",                     // multipart field name
                "test.png",                        // original filename
                "image/png",                       // content type
                "이미지".getBytes()                // content
        );
        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", "파일 업로드 테스트".getBytes());
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", "본문 내용입니다".getBytes());

        // multipart/form-data 요청 생성
        mockMvc.perform(multipart("/posts")
                        .file(image)
                        .param("title", "파일 업로드 테스트")    // ✅ 문자열 파라미터는 param() 사용
                        .param("content", "본문입니다")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated());


        // DB 확인
        Post savedPost = postRepository.findAll().get(0);
        List<FileAttachment> attachments = postRepository.searchOne(savedPost.getId()).get().getAttachments();
        assertThat(attachments).hasSize(1);

        // 파일 저장 경로 확인
        String filePath = attachments.get(0).getFilePath();
        File file = new File(filePath);
        assertThat(file.exists()).isTrue();
    }
}
