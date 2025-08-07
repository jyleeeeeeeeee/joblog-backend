package com.joblog.notification.kafka.producer;

import com.joblog.notification.kafka.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private static final String TOPIC = "notification-topic";

    public void send(NotificationEvent event) {
        log.info("[Kafka] 알림 이벤트 전송: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
