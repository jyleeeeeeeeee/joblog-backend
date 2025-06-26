package com.joblog.common;

import com.joblog.common.exception.DuplicateEmailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test/duplicate-email")
    public void throwException() {
        throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
    }
}