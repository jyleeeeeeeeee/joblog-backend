package com.joblog.trace.aop;

import com.joblog.trace.LogTrace;
import com.joblog.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace logTrace;

    // AOP 적용 대상 지정 (패키지 경로에 맞게 조절하세요)
    @Around("com.joblog.trace.aop.LogTracePointcuts.allApplication()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            String signature = joinPoint.getSignature().toShortString(); // 메서드 정보
            status = logTrace.begin(signature); // ✅ 진입 로그
            Object result = joinPoint.proceed(); // ✅ 실제 메서드 실행
            logTrace.end(status);               // ✅ 종료 로그
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);     // ✅ 예외 로그
            throw e;
        }
    }
}
