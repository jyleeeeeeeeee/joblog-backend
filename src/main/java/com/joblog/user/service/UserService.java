package com.joblog.user.service;

import com.joblog.comment.repository.CommentRepository;
import com.joblog.common.exception.DuplicateEmailException;
import com.joblog.common.exception.UserNotFoundException;
import com.joblog.mypage.dto.UserLikedPostResponse;
import com.joblog.post.repository.PostRepository;
import com.joblog.postlike.repository.PostLikeRepository;
import com.joblog.user.dto.MyCommentResponse;
import com.joblog.user.dto.MyPostResponse;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.dto.UserResponse;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(UserJoinRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        String encoded = passwordEncoder.encode(request.password());
        User user = User.of(request.email(), encoded, request.nickname());
        userRepository.save(user);
    }

    public UserResponse getUserInfo(String email) {// 이메일로 사용자 조회. 없으면 예외
        User user = findUser(email);

        // DTO 변환
        return new UserResponse(user.getEmail(), user.getNickname());
    }

    public Page<MyPostResponse> getMyPosts(String email, Pageable pageable) {
        User user = findUser(email);
        return postRepository.findByUser(user, pageable).map(MyPostResponse::new);
    }

    public Page<MyCommentResponse> getMyComments(String email, Pageable pageable) {
        User user = findUser(email);
        return commentRepository.findByUser(user, pageable).map(MyCommentResponse::new);
    }

    public List<UserLikedPostResponse> getLikedPosts(User user) {
        return postLikeRepository.findAllByUser(user).stream().map(postLike -> new UserLikedPostResponse(postLike.getPost())).toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }


}
