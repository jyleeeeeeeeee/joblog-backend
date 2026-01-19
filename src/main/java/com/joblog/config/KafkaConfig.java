package com.joblog.config;

import com.joblog.notification.domain.repository.NotificationRepository;
import com.joblog.notification.kafka.consumer.NotificationKafkaConsumer;
import com.joblog.notification.kafka.event.NotificationEvent;
import com.joblog.notification.kafka.producer.NotificationKafkaProducer;
import lombok.RequiredArgsConstructor;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

    // Kafka 브로커 주소 (Docker 환경이면 'kafka:9092'로 변경 가능)
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
//    private final NotificationRepository notificationRepository;
//    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;


    // =========================
    // ✅ Producer 설정
    // =========================
    @Bean // ProducerFactory Bean 등록
    public ProducerFactory<String , NotificationEvent> producerFactory() {
        // Producer 설정값 저장용
        HashMap<String, Object> config = new HashMap<>();

        // Kafka 브로커 주서 설정
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 메시지 값 직렬화 : 문자열 -> 바이트
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 메시지 값 직렬화 : 객체 -> JSON
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerializer.class);

        // ProducerFactory 생성
        return new DefaultKafkaProducerFactory<>(config);
    }


    @Bean // KafkaTemplate Bean 등록
    public KafkaTemplate<String, NotificationEvent> kafkaTemplate() {
        // KafkaTemplate : 메시지를 Kafka 토픽으로 전송
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean // ConsumerFactory Bean 등록
    public ConsumerFactory<String ,NotificationEvent> consumerFactory() {
        // JSON → NotificationEvent 변환을 위한 JsonDeserializer 생성
        JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);

        // 모든 패키지의 클래스를 역직렬화 허용 (보안상 필요한 경우 특정 패키지만 허용 가능)
        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>(); // Consumer 설정값 저장용 Map 생성

        // Kafka 브로커 주소 설정
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // Consumer 그룹 ID 설정 (같은 그룹 ID를 가진 Consumer들은 메시지를 나눠서 받음)
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");

        // 메시지 키 역직렬화: 바이트를 문자열로 변환
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 메시지 값 역직렬화: JSON을 NotificationEvent 객체로 변환
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        // ConsumerFactory 생성 (설정값, 키/값 역직렬화 클래스 지정)
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean // KafkaListener 컨테이너 팩토리 Bean 등록
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> kafkaListenerContainerFactory() {
        // KafkaListenerContainerFactory 생성
        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // ConsumerFactory를 컨테이너 팩토리에 주입
        factory.setConsumerFactory(consumerFactory());

        // 완성된 컨테이너 팩토리 반환
        return factory;
    }

    // =========================
    // ✅ Kafka 토픽 생성
    // =========================
    @Bean // Kafka 토픽 Bean 등록
    public NewTopic notificationTopic() {
        // "notification-topic"이라는 이름의 토픽 생성
        // 파티션 개수: 1개, 복제 개수: 1개
        return new NewTopic("notification-topic", 1, (short) 1);
    }
}
