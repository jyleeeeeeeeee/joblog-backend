package com.joblog.notification.kafka.consumer;

import com.joblog.notification.domain.Notification;
import com.joblog.notification.domain.repository.NotificationRepository;
import com.joblog.notification.kafka.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class NotificationKafkaConsumer {
    private final NotificationRepository notificationRepository;

    // Kafka로부터 메시지를 수신하여 처리하는 메서드
    @KafkaListener(
            topics = "notification-topic", // 수신할 Kafka 토픽 이름
            groupId = "notification-group", // Consumer 그룹 ID
            containerFactory = "kafkaListenerContainerFactory" // JSON 역직렬화 설정된 팩토리 사용
    )
    public void listen(NotificationEvent event) {

        log.info("[Kafka] 알림 이벤트 수신: {}", event); // 수신 로그 출력

        // 수신된 이벤트를 Notification 엔티티로 변환 후 저장
        Notification notification = Notification.builder()
                .receiverId(event.getReceiverId())     // 수신자 ID
                .content(event.getContent())           // 알림 내용
                .type(event.getType())                 // 알림 종류
                .build();

        notificationRepository.save(notification); // DB에 저장
        log.info("[Kafka] 알림 저장 완료 - receiverId={}, type={}",
                event.getReceiverId(), event.getType());
    }
}
