package com.joblog.trace.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

public class LogTracePointcuts {

    // 컨트롤러, 서비스, 리포지토리 계층의 모든 메서드
    @Pointcut("(execution(* com.joblog..controller..*(..)) || " +
            "execution(* com.joblog..service..*(..)) || " +
            "execution(* com.joblog..repository..*(..))) &&" +
            "!execution(* com.joblog.auth.oauth..*(..)) &&" +
            "!execution(* com.joblog.user.repository.UserRepository.findByEmailForOAuth())")
    public void allApplication() {

    }

    // 컨트롤러 계층의 모든 메서드
    @Pointcut("execution(* com.joblog..controller..*(..))")
    public void allController() {

    }

    // 서비스 계층의 모든 메서드
    @Pointcut("execution(* com.joblog..service..*(..))")
    public void allService() {

    }

    // 리포지토리 계층의 모든 메서드
    @Pointcut("execution(* com.joblog..repository..*(..))")
    public void allRepository() {

    }

    // 예시: 특정 패키지 전체 추적
    @Pointcut("within(com.joblog.post..*)")
    public void allDomain() {

    }
}
