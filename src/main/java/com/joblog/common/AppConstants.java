package com.joblog.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    // ✅ JWT 관련
    public static final String JWT_COOKIE_NAME = "JOBLOG_TOKEN"; // JWT가 저장될 쿠키 이름
    public static final long JWT_EXPIRATION_HOURS = 2; // JWT 만료 시간 (단위: 시간)

    // ✅ 사용자 관련
    public static final String ROLE_USER = "ROLE_USER"; // 기본 사용자 권한

    // ✅ HTTP 관련
    public static final String COOKIE_PATH = "/"; // 쿠키 경로
    public static final String COOKIE_SAMESITE = "Strict"; // SameSite 설정
    public static final String GOOGLE = "google";
    public static final String NAVER = "naver";
    public static final String KAKAO = "kakao";

    public static final String[] exceptURI = {
//            "/",
            "/login",
            "/auth/login",
            "/auth/session-check",
            "/users/join",
            "/session/login",
            "/favicon.ico", "/apple-touch-icon.png", "/apple-touch-icon-precomposed.png", "/default-ui.css"
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/swagger-resources/**",
//            "/v3/api-docs/**",
//            "/v3/api-docs",
//            "/webjars/**"
    };
}