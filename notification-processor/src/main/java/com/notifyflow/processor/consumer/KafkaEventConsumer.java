package com.notifyflow.processor.consumer;

import com.notifyflow.processor.model.Notification;
import com.notifyflow.processor.repository.NotificationRepository;
import com.notifyflow.processor.service.RabbitMQPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {

    private final NotificationRepository notificationRepository;
    private final RabbitMQPublisherService rabbitMQPublisherService;

    @KafkaListener(topics = "${kafka.topic.notification-events}", groupId = "notification-processor-group")
    public void consume(Map<String, String> eventPayload) {
        log.info("Received event from Kafka: {}", eventPayload);

        Notification notification = new Notification();
        notification.setUserId(eventPayload.get("userId"));
        notification.setEventType(eventPayload.get("eventType"));
        notification.setMessage(eventPayload.get("message"));

        Notification saved = notificationRepository.save(notification);
        log.info("Notification saved to PostgreSQL with id={}", saved.getId());

        rabbitMQPublisherService.publish(saved);
    }
}
