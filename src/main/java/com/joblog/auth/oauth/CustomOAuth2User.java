package com.joblog.auth.oauth;

import com.joblog.auth.CustomUserDetails;
import com.joblog.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User extends CustomUserDetails implements OAuth2User {

    private final transient Map<String, Object> attributes; // 구글에서 내려준 사용자 정보

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        super(user); // 부모 생성자 호출 (UserDetails 필드 채움)
        this.attributes = attributes;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2User로서 사용자 정보 리턴
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한은 Spring Security에서 필수로 요구하므로 최소 하나라도 있어야 함
        return Collections.singleton(() -> "ROLE_USER");
    }

    @Override
    public String getName() {
        // 사용자 식별자 반환 (기본으로 "sub", 또는 "email")
        return getUser().getEmail(); // 또는 attributes.get("sub").toString()
    }
}
