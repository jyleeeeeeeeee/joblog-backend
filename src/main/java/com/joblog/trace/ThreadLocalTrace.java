package com.joblog.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ThreadLocalTrace implements LogTrace{
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private static final ThreadLocal<Integer> levelHolder = ThreadLocal.withInitial(() -> 0);


    @Override
    public TraceStatus begin(String message) {
        int level = levelHolder.get();
        levelHolder.set(level + 1);
        String traceId = UUID.randomUUID().toString();
        long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {} {}", traceId, addSpace(START_PREFIX, level), message);
        return new TraceStatus(traceId, startTimeMs, message, level);
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        long stopTime = System.currentTimeMillis();
        long duration = stopTime - status.getStartTimeMs();
        int level = status.getLevel();
        levelHolder.set(level - 1);

        if (e == null) {
            log.info("[{}] {}{} time={}ms", status.getTraceId(), addSpace(COMPLETE_PREFIX, level), status.getMessage(), duration);
        } else {
            log.warn("[{}] {}{} time={}ms ex={}", status.getTraceId(), addSpace(EX_PREFIX, level), status.getMessage(), duration, e.toString());
        }
    }

    private String addSpace(String prefix, int level) {
        return " ".repeat(level * 2) + prefix + " ";
    }
}
