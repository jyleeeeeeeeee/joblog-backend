package com.joblog.common.resolver;

import com.joblog.auth.CustomUserDetails;
import com.joblog.auth.jwt.JwtProvider;
import com.joblog.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) &&
                parameter.getParameterType().equals(CustomUserDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        log.info("principal = {}", principal);

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser(); // ✅ 실제 User 반환
        }

        return null;

    }
}
