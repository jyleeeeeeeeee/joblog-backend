package com.joblog.post.controller;

import com.joblog.auth.CustomUserDetails;
import com.joblog.post.domain.Post;
import com.joblog.post.dto.PostRequest;
import com.joblog.post.dto.PostResponse;
import com.joblog.post.dto.PostSearchCondition;
import com.joblog.post.dto.PostUpdateRequest;
import com.joblog.post.service.PostService;
import com.joblog.postlike.service.PostLikeService;
import com.joblog.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;


    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        User user = userDetails.getUser();
        PostRequest request = new PostRequest(title, content);
        Long postId = postService.create(request, attachments, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }


    @GetMapping
    public ResponseEntity<Page<PostResponse>> searchPost(
            @ModelAttribute PostSearchCondition condition, Pageable pageable) {
        Page<PostResponse> posts = postService.searchPost(condition, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> searchPost(@PathVariable Long id) {
        PostResponse postResponse = postService.searchPostOne(id);

        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody @Valid PostUpdateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        postService.update(id, request, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        postService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    // ✅ 게시글 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postLikeService.like(userDetails.getUser(), postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ 게시글 좋아요 취소
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postLikeService.unlike(userDetails.getUser(), postId);
        return ResponseEntity.noContent().build();
    }

    // ✅ 게시글 좋아요 수 조회
    @GetMapping("/{postId}/likes")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        long count = postLikeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }
}
