package com.joblog.config;

import com.joblog.auth.jwt.JwtAuthenticationFilter;
import com.joblog.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // @PreAuthorize 등 메서드 수준 보안 허용
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 필터
    private final CustomUserDetailsService userDetailsService;

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
//                .authorizeHttpRequests(auth -> auth
//                                // GET /posts 는 모두에게 허용
//                                .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
//                                // POST /posts 는 인증된 사용자만 허용
//                                .requestMatchers(HttpMethod.POST, "/posts/**").authenticated()
//                                // PUT /posts/** 는 인증된 사용자만 허용
//                                .requestMatchers(HttpMethod.PUT, "/posts/**").authenticated()
//                                // DELETE /posts/** 도 인증 필요
//                                .requestMatchers(HttpMethod.DELETE, "/posts/**").authenticated()
//                                // 로그인, 회원가입은 인증 없이 허용
//                                .requestMatchers("/auth/login", "/users/join", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                                // 그 외는 모두 인증 필요
//                         .anyRequest().authenticated() // 실 서비스에서는 이걸로 바꿔야 함
//                )
                .authorizeHttpRequests(auth -> auth
                        // ✅ 인증 없이 허용할 경로
                        .requestMatchers(
                                "/auth/login",
                                "/users/join"
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/swagger-resources/**",
//                                "/v3/api-docs/**",
//                                "/v3/api-docs",
//                                "/webjars/**"
                        ).permitAll()
                        // ✅ 게시글 관련 (로그인 유저만 가능)
                        .requestMatchers(HttpMethod.POST, "/posts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/posts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("USER")

                        // ✅ 댓글 관련 (로그인 유저만 가능)
                        .requestMatchers(HttpMethod.POST, "/comments/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").hasRole("USER")

                        // ✅ 좋아요 기능
                        .requestMatchers("/posts/*/like").hasRole("USER")

                        // ✅ 마이페이지
                        .requestMatchers("/me/**").hasRole("USER")

                        // ✅ 관리자만 가능한 경로
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ✅ 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )


                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 먼저 동작
        ;
        return http.build();
    }
    /**
     * AuthenticationManager 빈 등록 (로그인 시 사용)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // DB 사용자 인증 제공자
        provider.setUserDetailsService(userDetailsService); // 사용자 조회
        provider.setPasswordEncoder(passwordEncoder());     // 비밀번호 암호화 방식
        return provider;
    }



    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public CommandLineRunner logFilters(FilterChainProxy filterChainProxy) {
        return args -> {
            System.out.println("📦 Registered Filters:");
            filterChainProxy.getFilterChains().forEach(chain -> {
                chain.getFilters().forEach(filter ->
                        System.out.println(" - " + filter.getClass().getName()));
            });
        };
    }

}
