package com.joblog.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/favicon.ico")
    @ResponseBody
    public ResponseEntity<Void> favicon2() {
        return ResponseEntity.noContent().build(); // 204 No Content
    }

//    @GetMapping("/favicon.ico")
    @ResponseBody
    public String favicon3() {
        // 204 No Content를 원한다면 상태 코드는 따로 설정 불가 → 기본 200 OK
        // 여기서는 단순히 본문 없이 빈 문자열 반환
        return "";
    }
}
