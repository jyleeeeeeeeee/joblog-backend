package com.joblog.auth.jwt;

import com.joblog.auth.CustomUserDetails;
import com.joblog.auth.CustomUserDetailsService;
import com.joblog.common.exception.JwtInvalid401Exception;
import com.joblog.common.exception.JwtInvalid403Exception;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // JWT 유틸
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       /* log.info("🔍 Filtering URI: {}", request.getRequestURI());
        String requestURI = request.getRequestURI();
        // ✅ JWT 인증 없이 통과시킬 경로 (Spring Security permitAll()과 동일하게)
        if (requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.equals("/auth/login")
                || requestURI.startsWith("/swagger-resources")
                || requestURI.startsWith("/webjars")
                || requestURI.equals("/swagger-ui.html") ||
                requestURI.equals("/users/join")) {
            log.info("Swagger 등 공개 URI 요청: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }*/


        // 1. Authorization 헤더에서 JWT 추출
        String token = jwtProvider.resolveToken(request);
        // ✅ 토큰이 없으면 그냥 다음 필터로 넘김 (로그인 안 한 사용자도 접근 가능한 페이지 대비)
        if (token == null) {
            log.info("⚠️ No JWT token - allowing through");
            filterChain.doFilter(request, response);
            return;
        }



        if (!jwtProvider.isValidToken(token)) {
            throw new JwtInvalid403Exception("유효하지 않은 JWT 토큰입니다.");
        }

        // 2. 토큰이 유효하면 이메일로 사용자 조회
        String email = jwtProvider.getEmailFromToken(token);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        // 3. 인증 객체 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 4. 인증 정보 SecurityContext에 저장
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("userPrincipal = {}", request.getUserPrincipal());
        // 5. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 헤더가 "Bearer {token}" 형식인지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 문자열 리턴
        }
        return null;
    }
}
