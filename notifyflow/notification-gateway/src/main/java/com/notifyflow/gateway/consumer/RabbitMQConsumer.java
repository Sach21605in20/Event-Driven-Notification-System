package com.notifyflow.gateway.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(Map<String, Object> notification) {
        String userId = (String) notification.get("userId");
        log.info("Received notification from RabbitMQ for userId={}", userId);

        // Send to WebSocket topic specific to this user
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
        log.info("Notification delivered via WebSocket to userId={}", userId);
    }

    @RabbitListener(queues = "${rabbitmq.dlq}")
    public void handleDeadLetter(Map<String, Object> notification) {
        String userId = (String) notification.get("userId");
        log.error("DEAD LETTER: Notification permanently failed after retries. userId={}, notification={}",
                userId, notification);
        // In production: alert ops team, store failure record, etc.
    }
}
