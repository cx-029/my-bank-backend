package com.mybank.backend.repository;

import com.mybank.backend.entity.NotificationLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLikeRepository extends JpaRepository<NotificationLike, Long> {
    int countByNotificationId(Long notificationId);
    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
}