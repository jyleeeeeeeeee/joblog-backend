package com.joblog.auth.service;

import com.joblog.auth.dto.LoginRequest;
import com.joblog.common.exception.UnauthorizedException;
import com.joblog.auth.jwt.JwtProvider;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private Key key;
    @Value("${jwt.secret}")
    private String secretKey;
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 이메일입니다."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        if(user.isOauthUser()) {
            throw new UnauthorizedException("소셜 로그인 계정입니다.");
        }

        String email = user.getEmail();
        List<String> roles = user.getRoles();
        return jwtProvider.createToken(email, roles);
    }
}
