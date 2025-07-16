package com.mybank.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class NotificationComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}