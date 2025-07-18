package com.mybank.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class NotificationLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;
    private Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}//