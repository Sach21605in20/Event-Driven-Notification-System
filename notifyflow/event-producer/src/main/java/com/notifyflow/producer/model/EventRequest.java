package com.notifyflow.producer.model;

import lombok.Data;

@Data
public class EventRequest {
    private String userId;
    private String eventType;   // e.g. ORDER_PLACED, PAYMENT_FAILED, SYSTEM_ALERT
    private String message;
}
