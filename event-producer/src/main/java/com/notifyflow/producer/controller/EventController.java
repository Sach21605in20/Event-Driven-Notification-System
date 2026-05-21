package com.notifyflow.producer.controller;

import com.notifyflow.producer.model.EventRequest;
import com.notifyflow.producer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping
    public ResponseEntity<String> publishEvent(@RequestBody EventRequest request) {
        kafkaProducerService.publishEvent(request);
        return ResponseEntity.ok("Event published to Kafka: " + request.getEventType());
    }
}
