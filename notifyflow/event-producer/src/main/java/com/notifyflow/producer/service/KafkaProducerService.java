package com.notifyflow.producer.service;

import com.notifyflow.producer.model.EventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, EventRequest> kafkaTemplate;

    @Value("${kafka.topic.notification-events}")
    private String topic;

    public void publishEvent(EventRequest event) {
        log.info("Publishing event to Kafka: type={}, userId={}", event.getEventType(), event.getUserId());
        kafkaTemplate.send(topic, event.getUserId(), event);
        log.info("Event published successfully");
    }
}
