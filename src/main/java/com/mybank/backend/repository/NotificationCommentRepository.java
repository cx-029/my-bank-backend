package com.mybank.backend.repository;

import com.mybank.backend.entity.NotificationComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationCommentRepository extends JpaRepository<NotificationComment, Long> {
    List<NotificationComment> findByNotificationIdAndDeleted(Long notificationId, Integer deleted);

    List<NotificationComment> findByParentIdAndDeleted(Long parentId, Integer deleted);

    // 查询一级评论
    List<NotificationComment> findByNotificationIdAndParentIdAndDeleted(Long notificationId, Long parentId, Integer deleted);
}