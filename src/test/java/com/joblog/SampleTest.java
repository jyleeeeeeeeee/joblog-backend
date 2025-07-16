package com.joblog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

class SampleTest {
    @Test
    void failThisTest() {
        fail("일부러 실패시킴");
    }
}