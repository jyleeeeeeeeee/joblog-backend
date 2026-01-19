package com.joblog.config;

import com.joblog.auth.jwt.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class SecurityConfigLocal {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 필터
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return SecurityConfigCommon.securityFilterChain(http)
                .sessionManagement(session -> session.maximumSessions(1) // 동시 로그인 제한
                    .maxSessionsPreventsLogin(false)
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 먼저 동작
                .build();
    }

    @Bean
    public CommandLineRunner logFilters(FilterChainProxy filterChainProxy) {
        return args -> {
            System.out.println("========================📦 Registered Filters Start========================");
            filterChainProxy.getFilterChains().forEach(chain -> chain.getFilters().forEach(filter -> System.out.println(filter.getClass().getName())));
            System.out.println("========================📦 Registered Filters  End ========================");
        };
    }
}
