package com.joblog.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림을 받을 사용자 ID
    private Long receiverId;

    // 알림 내용 (예: "누가 내 게시글에 댓글을 남겼습니다")
    private String content;

    // 알림 종류 (댓글, 좋아요 등)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // 읽음 여부
    private boolean isRead;

    // 생성 시각
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
}
