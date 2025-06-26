package com.joblog.user.controller;

import com.joblog.common.resolver.LoginUser;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.dto.UserResponse;
import com.joblog.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid UserJoinRequest request) {
        userService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@LoginUser String email) {
        UserResponse response = userService.getUserInfo(email);
        return ResponseEntity.ok(response);
    }
}
