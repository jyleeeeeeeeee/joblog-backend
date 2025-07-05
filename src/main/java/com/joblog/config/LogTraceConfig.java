package com.joblog.config;

import com.joblog.trace.LogTrace;
import com.joblog.trace.ThreadLocalTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {
    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalTrace();
    }
}
