package com.joblog.notification.kafka.consumer;

import com.joblog.notification.domain.NotificationType;
import com.joblog.notification.domain.repository.NotificationRepository;
import com.joblog.notification.kafka.event.NotificationEvent;
import com.joblog.notification.kafka.producer.NotificationKafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationFlowIntegrationTest {
    @Autowired
    NotificationKafkaProducer producer;

    @Autowired
    NotificationRepository repository;

    @Test
    void producer_to_consumer_to_db() throws InterruptedException {
        producer.send(NotificationEvent.builder().receiverId(1L).content("통합 테스트 알림").type(NotificationType.COMMENT).build());

        Thread.sleep(1500);

        boolean exists = repository.findAll().stream().anyMatch(n -> "통합 테스트 알림".equals(n.getContent()));
        assertThat(exists).isTrue();
    }

}