package com.joblog.auth;

import com.joblog.common.exception.DuplicateEmailException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/session-check")
    public String sessionCheck(HttpSession session) {
        session.setAttribute("testKey", "testValue");
        return "sessionId : " + session.getId();
    }

    @GetMapping("/test/duplicate-email")
    public void throwException() {
        throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
    }
}
