package com.joblog.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joblog.auth.CustomUserDetails;
import com.joblog.auth.jwt.JwtAuthenticationFilter;
import com.joblog.config.SecurityConfigCommon;
import com.joblog.post.dto.PostRequest;
import com.joblog.post.service.PostService;
import com.joblog.postlike.service.PostLikeService;
import com.joblog.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PostController 입출력 검증 테스트
 */
@WebMvcTest(controllers = PostController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfigCommon.class, JwtAuthenticationFilter.class})
        })   // PostController만 로딩
@AutoConfigureMockMvc(addFilters = false) // ✅ Security Filter 비활성화)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // PostController의 의존성은 Mock으로 대체
    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostLikeService postLikeService;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc1NjI2OTA5OCwiZXhwIjoxNzU2Mjc2Mjk4fQ.EVub0FzpZbzRyHy6s5_GMY0L7R8WVIYCMuSmO3XWWc0";

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @DisplayName("게시글 생성 - JSON + 파일 업로드 성공")
    void createPostwithAttachment() throws Exception {
        // given
        User user = new User(1L, "test@example.com", "password",
                "nickname", new ArrayList<>(), new ArrayList<>(), false, new ArrayList<>()); // 엔티티 or 도메인 User
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // given
        String json = objectMapper.writeValueAsString(new PostRequest("test title", "test content"));
        MockMultipartFile requestPart = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                json.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("attachments",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes());
        // when
        mockMvc.perform(multipart("/posts")
                .file(requestPart).file(filePart)
                .with(authentication(auth))
                .header("Authorization", "Bearer " + token))   // ✅ CustomUserDetails를 SecurityContext에 강제 주입
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test title"))
                .andExpect(jsonPath("$.content").value("test content"));
        // then
    }
}