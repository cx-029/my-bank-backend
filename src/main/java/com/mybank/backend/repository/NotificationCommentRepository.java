package com.mybank.backend.repository;

import com.mybank.backend.entity.NotificationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationCommentRepository extends JpaRepository<NotificationComment, Long>, JpaSpecificationExecutor<NotificationComment> {
    List<NotificationComment> findByNotificationIdAndDeleted(Long notificationId, Integer deleted);

    List<NotificationComment> findByParentIdAndDeleted(Long parentId, Integer deleted);

    // 查询一级评论
    List<NotificationComment> findByNotificationIdAndParentIdAndDeleted(Long notificationId, Long parentId, Integer deleted);

    // 批量物理删除某通知的所有评论
    @Modifying
    @Query("DELETE FROM NotificationComment nc WHERE nc.notificationId = :notificationId")
    void deleteByNotificationId(Long notificationId);
}