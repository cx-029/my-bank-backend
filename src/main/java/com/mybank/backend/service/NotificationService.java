package com.mybank.backend.service;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();
    Notification getNotificationDetail(Long id);
    List<NotificationComment> getComments(Long notificationId);
    void addComment(Long notificationId, Long userId, String comment);
    int getLikeCount(Long notificationId);
    boolean likeNotification(Long notificationId, Long userId);
    boolean hasUserLiked(Long notificationId, Long userId);
    boolean unlikeNotification(Long notificationId, Long userId);
    boolean deleteComment(Long commentId, Long userId, boolean isAdmin);
    boolean likeComment(Long commentId, Long userId);
    boolean unlikeComment(Long commentId, Long userId);
    int getCommentLikeCount(Long commentId);
    boolean hasUserLikedComment(Long commentId, Long userId);
    List<NotificationComment> getChildComments(Long parentId);
    void addComment(Long notificationId, Long userId, String comment, Long parentId);

    // 新增分页方法
    Page<Notification> getNotificationsPaged(Pageable pageable);

    // 新增CRUD
    Notification saveNotification(Notification notification);
    Notification updateNotification(Notification notification);
    boolean deleteNotification(Long id);
}