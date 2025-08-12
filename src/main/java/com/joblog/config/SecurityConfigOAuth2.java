package com.joblog.config;

import com.joblog.auth.jwt.JwtAuthenticationFilter;
import com.joblog.auth.oauth.CustomOAuth2UserService;
import com.joblog.auth.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Profile({"staging","prod"})
public class SecurityConfigOAuth2 {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 필터
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return SecurityConfigCommon.securityFilterChain(http)
                .oauth2Login(oauth -> oauth
                    .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                    .loginPage("/oauth2/authorization/google")
                    .defaultSuccessUrl("/users/me", true)
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 먼저 동작
                .build();
    }

}
