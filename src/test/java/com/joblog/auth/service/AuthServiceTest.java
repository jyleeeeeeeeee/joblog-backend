package com.joblog.auth.service;

import com.joblog.auth.dto.LoginRequest;
import com.joblog.common.exception.UnauthorizedException;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.repository.UserRepository;
import com.joblog.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰을 반환한다.")
    void loginSuccess() {
        // given: 회원가입 선행
        UserJoinRequest joinRequest = new UserJoinRequest("login@test.com", "1234", "로그인유저");
        userService.join(joinRequest);

        // when: 로그인 요청
        LoginRequest loginRequest = new LoginRequest("login@test.com", "1234");
        String token = authService.login(loginRequest);

        // then: 토큰 반환됨
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
    void loginInvalidEmail() {
        // given
        LoginRequest loginRequest = new LoginRequest("login@test.com", "1234");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest)).isInstanceOf(UnauthorizedException.class)
                .hasMessage("존재하지 않는 이메일입니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
    void loginInvalidPassword() {
        // given: 회원가입 선행
        UserJoinRequest joinRequest = new UserJoinRequest("login@test.com", "1234", "로그인유저");
        userService.join(joinRequest);

        // when & then
        LoginRequest loginRequest = new LoginRequest("login@test.com", "wrong");
        assertThatThrownBy(() -> authService.login(loginRequest)).isInstanceOf(UnauthorizedException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

}