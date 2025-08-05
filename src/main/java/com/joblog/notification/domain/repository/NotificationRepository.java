package com.joblog.notification.domain.repository;

import com.joblog.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 유저의 알림 목록 조회 (최신순)
    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // 읽지 않은 알림 수
    Long countByReceiverIdAndIsReadFalse(Long receiverId);
}
