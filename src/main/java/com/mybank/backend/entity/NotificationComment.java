package com.mybank.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

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

    // 新增：父评论ID
    private Long parentId;

    // 新增：逻辑删除
    private Integer deleted = 0;
}//