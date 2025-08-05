package com.joblog.notification.kafka.event;

import com.joblog.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent implements Serializable {
    private Long receiverId;           // 알림 대상 사용자 ID
    private String content;            // 알림 내용
    private NotificationType type;     // 알림 종류
}
