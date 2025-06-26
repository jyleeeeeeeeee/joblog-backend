package com.joblog.user.service;

import com.joblog.common.exception.DuplicateEmailException;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("이미 존재하는 이메일로 가입하면 예외 발생")
    void duplicateEmail() {
        // given: 동일한 이메일로 2번 회원가입 시도
        userService.join(new UserJoinRequest("dupe@example.com", "pw", "dup1"));

        // when & then: 중복된 이메일로 가입 시 예외 발생
        assertThatThrownBy(() ->
                userService.join(new UserJoinRequest("dupe@example.com", "pw", "dup2"))
        ).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("회원가입 성공 시 저장")
    void joinSuccess() {
        // given: 회원가입 요청 생성
        UserJoinRequest request = new UserJoinRequest(
                "test@example.com", "1234", "테스트유저"
        );

        // when: 회원가입 실행
        userService.join(request);

        // then: DB에 저장된 유저가 존재하는지 확인
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(user.getNickname()).isEqualTo("테스트유저");
        assertThat(passwordEncoder.matches("1234", user.getPassword())).isTrue();
    }
}