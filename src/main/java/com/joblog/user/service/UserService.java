package com.joblog.user.service;

import com.joblog.common.exception.DuplicateEmailException;
import com.joblog.common.exception.UserNotFoundException;
import com.joblog.user.dto.UserJoinRequest;
import com.joblog.user.dto.UserResponse;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));


        // DTO 변환
        return new UserResponse(user.getEmail(), user.getNickname());
    }
}
