package com.joblog.config;

import com.joblog.auth.jwt.JwtAuthenticationFilter;
import com.joblog.auth.CustomUserDetailsService;
import com.joblog.auth.oauth.CustomOAuth2UserService;
import com.joblog.auth.oauth.OAuth2AuthenticationSuccessHandler;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.joblog.common.AppConstants.exceptURI;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // @PreAuthorize 등 메서드 수준 보안 허용
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 필터
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // ✅ PasswordEncoder 빈 등록

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .formLogin(form -> form
////                        .loginPage("/session/login") // 사용자 정의 로그인 페이지 (GET 요청)
//                        .loginProcessingUrl("/session/login") // 로그인 인증 처리 (POST 요청)
//                                .defaultSuccessUrl("/users/me", true)
//                        .permitAll()
//                )
                .formLogin(form -> form
                        .loginPage("/login-form") // 추후 커스텀 로그인 페이지 연결
                        .loginProcessingUrl("/auth/login") // 로그인 form action
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // 동시 로그인 제한
                        .maxSessionsPreventsLogin(false)
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 후 리다이렉트할 경로
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                // ✅ CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // ✅ 세션을 아예 사용하지 않음 (JWT 방식이기 때문에) X -> redis 사용으로 변경
                .sessionManagement(session -> session
//                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요 시 생성
                )
                .authorizeHttpRequests(auth -> auth
                        // ✅ 인증 없이 허용할 경로
                        .requestMatchers(exceptURI).permitAll()
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
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .loginPage("/oauth2/authorization/google") // 사용자가 수동으로 요청하는 주소
                        .defaultSuccessUrl("/users/me", true)       // ✅ 인증 성공 후 리다이렉트 목적지
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 먼저 동작
                // ✅ 사용자 인증 처리에 사용할 서비스 지정
                .userDetailsService(userDetailsService)
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
