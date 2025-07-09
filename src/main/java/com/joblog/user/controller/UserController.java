package com.joblog.user.controller;

import com.joblog.auth.CustomUserDetails;
import com.joblog.auth.model.AuthUser;
import com.joblog.common.resolver.LoginUser;
import com.joblog.mypage.dto.UserLikedPostResponse;
import com.joblog.user.dto.MyCommentResponse;
import com.joblog.user.dto.MyPostResponse;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.dto.UserResponse;
import com.joblog.user.entity.User;
import com.joblog.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse response = userService.getUserInfo(authUser.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<Page<MyPostResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<MyPostResponse> myPosts = userService.getMyPosts(userDetails.getUser().getEmail(), pageable);
        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/me/comments")
    public ResponseEntity<Page<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<MyCommentResponse> myComments = userService.getMyComments(userDetails.getUser().getEmail(), pageable);
        return ResponseEntity.ok(myComments);
    }

    @PostMapping("/likes")
    public List<UserLikedPostResponse> getMyLikes(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getLikedPosts(userDetails.getUser());
    }
}
