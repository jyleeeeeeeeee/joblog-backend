package com.joblog.auth.oauth;

import java.util.Locale;

public enum OAuthProvider {
    GOOGLE,
    NAVER,
    KAKAO;

    // 소문자 입력 대응 (google, naver 등)
    public static OAuthProvider from(String registrationId) {
        try {
            return OAuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth Provider: " + registrationId);
        }
    }
}
