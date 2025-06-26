package com.joblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {
    // ✅ PasswordEncoder 빈 등록

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // ✅ 세션을 아예 사용하지 않음 (JWT 방식이기 때문에)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // ✅ 인증, 인가 설정
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/login", "/users/join").permitAll() // 로그인, 회원가입은 인증 없이 허용
                                .anyRequest().permitAll() // 그 외 요청도 임시로 허용 (테스트 단계)
                        // .anyRequest().authenticated() // 실 서비스에서는 이걸로 바꿔야 함
                );
        return http.build();
    }
}
