package com.joblog.trace;

import java.util.UUID;

public class TraceId {
    private final String id;  // 고유 트랜잭션 ID
    private final int level;  // 호출 깊이
    public TraceId() {
        this.id = UUID.randomUUID().toString();
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }

    public TraceId createPreviousId() {
        return new TraceId(id, level - 1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
