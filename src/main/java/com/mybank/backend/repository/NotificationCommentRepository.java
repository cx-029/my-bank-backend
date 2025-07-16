package com.mybank.backend.repository;

import com.mybank.backend.entity.NotificationComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationCommentRepository extends JpaRepository<NotificationComment, Long> {
    List<NotificationComment> findByNotificationId(Long notificationId);
}