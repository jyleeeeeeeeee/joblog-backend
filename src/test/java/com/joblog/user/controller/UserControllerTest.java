package com.joblog.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joblog.auth.jwt.JwtProvider;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    ObjectMapper objectMapper;

    private String jwtToken;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("1234")
                .nickname("테스트유저")
                .roles(Collections.singletonList("USER"))
                .build());

        jwtToken = jwtProvider.createToken(user.getEmail(), user.getRoles());
    }
    @Test
    @DisplayName("JWT 로그인 후 /users/me 호출 시 사용자 정보 반환")
    void jwtAuthenticatedUserInfo() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(user.getNickname()));
    }

    @DisplayName("비로그인 상태에서 /users/me 요청 시 로그인 페이지로 리다이렉트 (302)")
    @Test
    void notAuthenticated_redirectToLogin() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isFound()) // ✅ 302
//                .andExpect(redirectedUrlPattern("**/oauth2/authorization/**")); // ✅ 리다이렉트 경로 검증
                .andExpect(redirectedUrlPattern("**/login")); // ✅ 리다이렉트 경로 검증
    }


}