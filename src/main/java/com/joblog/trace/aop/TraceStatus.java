package com.joblog.trace.aop;

import com.joblog.trace.TraceId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TraceStatus {
    private final TraceId traceId;
    private final Long startTimeMs;
    private final String message;

}
