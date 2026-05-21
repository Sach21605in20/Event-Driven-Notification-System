package com.notifyflow.gateway.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    private String id;
    private String userId;
    private String eventType;
    private String message;
    private boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
