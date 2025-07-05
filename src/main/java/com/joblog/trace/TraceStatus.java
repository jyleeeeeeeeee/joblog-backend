package com.joblog.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TraceStatus {
    private final String traceId;
    private final Long startTimeMs;
    private final String message;
    private final int level;
}
