package com.notifyflow.processor.service;

import com.notifyflow.processor.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publish(Notification notification) {
        log.info("Publishing notification to RabbitMQ: id={}, userId={}", notification.getId(), notification.getUserId());
        rabbitTemplate.convertAndSend(exchange, routingKey, notification);
    }
}
