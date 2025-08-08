package com.joblog.notification.kafka.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private Long receiverId;   // 알림 받을 사용자 ID
    private String content;    // 알림 내용
    private String type;       // 알림 종류 (ex: COMMENT, LIKE 등)
    private String url;        // 클릭 시 이동할 URL
}
