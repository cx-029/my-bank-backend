package com.mybank.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long commentId;
    private Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}