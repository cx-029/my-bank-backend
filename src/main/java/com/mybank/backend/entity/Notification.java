package com.mybank.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private String author;
}//