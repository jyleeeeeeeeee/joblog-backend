package com.joblog.trace;

import com.joblog.trace.aop.TraceStatus;

public interface LogTrace {
    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e);
}
