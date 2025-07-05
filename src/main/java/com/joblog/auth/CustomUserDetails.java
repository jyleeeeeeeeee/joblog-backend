package com.joblog.auth;

import com.joblog.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // 실제 사용자 엔티티
    private final User user;

    // 권한 반환 (기본은 비워둠)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // ✅ ROLE_ prefix 필요
                .map(role -> new SimpleGrantedAuthority(role)) // ✅ ROLE_ prefix 필요
                .collect(Collectors.toList());
    }


    // 비밀번호 반환 (스프링 시큐리티 내부 사용)
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자명 반환 (이메일을 ID로 사용하는 경우)
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
