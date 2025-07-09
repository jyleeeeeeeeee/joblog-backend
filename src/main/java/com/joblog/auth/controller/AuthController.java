package com.joblog.auth.controller;

import com.joblog.auth.dto.LoginRequest;
import com.joblog.auth.service.AuthService;
import com.nimbusds.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.joblog.common.AppConstants.JWT_COOKIE_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request,
                                      HttpServletResponse response) {
        String token = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                .httpOnly(true)
//                .secure(true)
                .secure(false) // ✅ HTTPS 아닌 로컬 테스트 위해 반드시 false
                .path("/")
                .maxAge(Duration.ofHours(2))
//        Strict: 로그인 후 리디렉션 시 쿠키 안 보냄 → JWT 인증 불가
//        Lax: GET 리디렉션 시 쿠키 전송 허용됨
//        None: 쿠키를 항상 전송 (단, 이 경우 secure=true 필수임 → 로컬에서는 안 맞음)
                .sameSite("Lax")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }
    @GetMapping("/session-check")
    public String sessionCheck(HttpSession session) {
        session.setAttribute("testKey", "testValue");
        return "sessionId: " + session.getId();
    }

}
