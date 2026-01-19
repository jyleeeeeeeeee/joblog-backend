package com.joblog.auth.jwt;

import com.joblog.auth.CustomUserDetails;
import com.joblog.auth.CustomUserDetailsService;
import com.joblog.common.exception.ErrorResponse;
import com.joblog.common.exception.JwtInvalid401Exception;
import com.joblog.common.exception.JwtInvalid403Exception;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PatternParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.joblog.common.AppConstants.JWT_COOKIE_NAME;
import static com.joblog.common.AppConstants.exceptURI;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // JWT 유틸
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("🔍 Filtering URI: {}", request.getRequestURI());
        // 1. Authorization 헤더에서 JWT 추출
        String token = resolveToken(request);
        // ✅ 토큰이 없으면 그냥 다음 필터로 넘김 (로그인 안 한 사용자도 접근 가능한 페이지 대비)
        if (token == null) {
            log.info("⚠️ No JWT token - allowing through");
            try {
                filterChain.doFilter(request, response);
                return;
            } catch (PatternParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> validMap = jwtProvider.isValidToken(token);
        if (!(Boolean) validMap.get("isValid")) {
            Exception e = (Exception) validMap.get("exception");
            HttpStatus status;
            if (e instanceof JwtInvalid401Exception) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (e instanceof JwtInvalid403Exception) {
                status = HttpStatus.FORBIDDEN;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse(status.name(), e.getMessage());

            response.getWriter().write(errorResponse.toString());
            return;
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


    // HTTP 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        System.out.println("request.getRequestURI() = " + request.getRequestURI());
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // ✅ 쿠키에서 JWT 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
