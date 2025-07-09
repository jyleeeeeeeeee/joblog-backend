package com.joblog.auth.oauth;

import com.joblog.auth.jwt.JwtProvider;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

import static com.joblog.common.AppConstants.JWT_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // ✅ CustomOAuth2User에서 유저 객체 꺼냄
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // ✅ JWT 생성
        String token = jwtProvider.createToken(user.getEmail(), user.getRoles());

        // ✅ 쿠키로 응답에 추가
        ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                .httpOnly(true)
//                .secure(true)
                .secure(false) // ✅ HTTPS 아닌 로컬 테스트 위해 반드시 false
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // ✅ 리다이렉트 (인증 성공 후 페이지)
        response.sendRedirect("/users/me");
    }
}
