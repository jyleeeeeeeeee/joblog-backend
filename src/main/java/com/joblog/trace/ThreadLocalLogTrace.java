package com.joblog.trace;

import com.joblog.trace.aop.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";

    private final ThreadLocal<TraceId> traceHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId(); // traceId 생성 또는 레벨 증가
        TraceId traceId = traceHolder.get();
        Long startTimeMs = System.currentTimeMillis();

        int level = traceId.getLevel();
        log.info("Lv.{} [{}] {}{}", level, traceId.getId(), addSpace(START_PREFIX, level), message);

        return new TraceStatus(traceId, startTimeMs, message);
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
        Long stopTimeMs = System.currentTimeMillis();
        long resultTime = stopTimeMs - status.getStartTimeMs();

        TraceId traceId = status.getTraceId();

        int level = traceId.getLevel();
        if (e == null) {
            log.info("Lv.{} [{}] {}{} time={}ms", level, traceId.getId(),
                    addSpace(COMPLETE_PREFIX, level),
                    status.getMessage(),
                    resultTime
            );
        } else {
            log.info("Lv.{} [{}] {}{} time={}ms ex={}", level, traceId.getId(),
                    addSpace(EXCEPTION_PREFIX, level),
                    status.getMessage(),
                    resultTime,
                    e.toString()
            );
        }

        releaseTraceId(); // 호출 레벨 감소 또는 제거
    }

    private void syncTraceId() {
        TraceId traceId = traceHolder.get();
        if (traceId == null) {
            traceHolder.set(new TraceId()); // 최초 호출
        } else {
            traceHolder.set(traceId.createNextId()); // 내부 호출
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceHolder.get();
        if (traceId.isFirstLevel()) {
            traceHolder.remove(); // 마지막이면 제거
        } else {
            traceHolder.set(traceId.createPreviousId()); // 레벨 감소
        }
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(i == level - 1 ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
