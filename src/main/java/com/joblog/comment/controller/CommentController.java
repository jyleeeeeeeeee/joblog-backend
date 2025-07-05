package com.joblog.comment.controller;

import com.joblog.auth.CustomUserDetails;
import com.joblog.comment.dto.CommentRequest;
import com.joblog.comment.dto.CommentResponse;
import com.joblog.comment.service.CommentService;
import com.joblog.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 등록
     */
    @PostMapping
    public ResponseEntity<Void> create(@AuthenticationPrincipal CustomUserDetails userDetails, // 인증된 사용자 주입
                                       @RequestBody @Valid CommentRequest request) {
        User user = userDetails.getUser(); // 실제 User 엔티티 추출
        commentService.create(user, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable Long commentId,
                                       @RequestBody @Valid CommentRequest request) {
        User user = userDetails.getUser(); // 실제 User 엔티티 추출
        commentService.update(user, commentId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 삭제 (soft delete)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable Long commentId) {
        User user = userDetails.getUser(); // 실제 User 엔티티 추출
        commentService.delete(user, commentId);
        return ResponseEntity.ok().build();
    }

}
