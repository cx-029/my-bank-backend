package com.mybank.backend.repository;

import com.mybank.backend.entity.NotificationLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationLikeRepository extends JpaRepository<NotificationLike, Long> {
    int countByNotificationId(Long notificationId);
    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
    NotificationLike findByNotificationIdAndUserId(Long notificationId, Long userId);

    // 批量物理删除某通知的所有点赞
    @Modifying
    @Query("DELETE FROM NotificationLike nl WHERE nl.notificationId = :notificationId")
    void deleteByNotificationId(Long notificationId);
}